<template>
  <div class="index">
    <slot name="header" v-if="ready"></slot>
    <el-skeleton v-else class="flex items-center gap-6" animated>
      <template #template>
        <el-skeleton-item variant="h1" style="width: 10%;"/>
        <el-skeleton-item variant="h1" style="width: 20%;"/>
      </template>
    </el-skeleton>
    <!-- 骨架屏第一次加载时显示 -->
    <el-skeleton class="anime-list" :loading="loading&&!page||!ready" animated :count="pageSize" :throttle="1500">
      <template #template>
        <div class="anime-item">
          <slot name="skeleton"></slot>
          <el-skeleton-item variant="h3" class="mt-2"/>
          <el-skeleton-item variant="text" style="width: 30%;"/>
        </div>
      </template>
      <template v-if="page && ready">
        <div v-if="page.current>1" @click="prevPage" class="prev iconfont icon-zuojiantou"></div>
        <div v-if="!done" @click="nextPage" class="next iconfont icon-youjiantou"></div>
        <async :empty="page?.empty" min-h="200px">
          <div class="anime-list">
            <transition-group :name="animClass" :duration="{enter:600,leave:300}">
              <!-- 让每轮key都不会重复,防止过渡动画不生效 -->
              <div v-for="i in records" :key="uuidv4()" class="anime-item">
                <slot name="item" :item="i"></slot>
                <div class="text-bold text-lg maxline-1 my-1 video-title" @click="play(i)">{{ i.video.title }}</div>
                <div class="maxline-1 text-sm grey2">{{ i.desc }}</div>
              </div>
            </transition-group>
          </div>
        </async>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import { clamp, uniqueId } from 'lodash';
import { v4 as uuidv4 } from 'uuid';
const props = defineProps<{ 
  request: (pageNum: number, pageSize: number)=>Promise<Page<Bangumi>>,
  min: number //最小展示个数
  max: number, //最大展示个数
  ready: boolean, //第一次渲染是否准备好
}>()
const { width } = useWindowSize()
//一行显示的数量,根据屏幕最大宽度-实际宽度后计算
const pageSize = computed(()=>{
  return clamp(props.max - Math.floor((screen.width - width.value) / 200), props.min, props.max)
})
//最大宽度=(100-gap)/元素个数
const maxWidth = computed(()=>(100-(pageSize.value-1))/pageSize.value+'%')
//根据不同方向使用不同的过渡效果
const animClass = ref('none')
//取消因为宽度变化,而触发的过渡动画
watch(pageSize, ()=>{
  animClass.value = 'none'
})
const { page, loading, done, prev, next, run } = usePage((pageNum)=>props.request(pageNum, pageSize.value), {})
function prevPage() {
  animClass.value = 'list-left'
  prev()
}
function nextPage() {
  animClass.value = 'list-right'
  next()
}
//展示的数据列表
const records = computed(()=>page.value?.records?.toSpliced(pageSize.value)||[])
next()
function play(bangumi: Bangumi) {
  const target = bangumi.video.history?.part || bangumi.video
  window.open(target.uri, '_blank')
}
defineExpose({
  reset() {
    animClass.value = 'list-replace'
    run(1)
  },
  play,
})
</script>

<style scoped lang="scss">
.index {
  position: relative;
}
.prev, .next {
  position: absolute;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #f1f1f1;
  cursor: pointer;
  user-select: none;
  transition: scale .3s;
  top: 50%;
  z-index: 10;
  &:hover {
    scale: 1.1;
  }
  &::before {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translateX(-50%) translateY(-50%);
  }
}
.next {
  right: 0;
  transform: translateX(30%) translateY(-50%);
}
.prev {
  left: 0;
  transform: translateX(-30%) translateY(-50%);
}
.anime-list {
  display: flex;
  column-gap: 1%;
  margin-top: 10px;
  overflow: hidden;
  .anime-item {
    flex: 1;
    max-width: v-bind(maxWidth); //防止元素太少,宽度过大
    &:hover .video-title {
      color: var(--blue0);
    }
  }
  .video-title {
    cursor: pointer;
    user-select: none;
    transition: color .3s;
  }
}
</style>

<!-- 向后翻页 -->
<style scoped>
.list-right-move, /* 对移动中的元素应用的过渡 */
.list-right-enter-active,
.list-right-leave-active {
  transition: all .3s ease;
}
.list-right-enter-active{
  transition-delay: .3s;
}
.list-right-enter-from {
  opacity: 0;
  transform: translateX(100px);
}
.list-right-leave-to {
  opacity: 0;
  transform: translateX(-100px);
}
.list-right-leave-active {
  /* 防止页面抖动 */
  position: absolute; 
}
</style>

<!-- 向左翻页 -->
<style scoped>
.list-left-move, /* 对移动中的元素应用的过渡 */
.list-left-enter-active,
.list-left-leave-active {
  transition: all .3s ease;
}
.list-left-enter-active{
  transition-delay: .3s;
}
.list-left-enter-from {
  opacity: 0;
  transform: translateX(-100px);
}
.list-left-leave-to {
  opacity: 0;
  transform: translateX(100px);
}
.list-left-leave-active {
  /* 防止页面抖动 */
  position: absolute;
  /* transition: all .1s ease; */
}
</style>

<!-- 替换所有元素时 -->
<style scoped>
.list-replace-move, /* 对移动中的元素应用的过渡 */
.list-replace-enter-active,
.list-replace-leave-active {
  transition: all .3s ease;
}
.list-replace-enter-active{
  /* transition-delay: .3s; */
}
.list-replace-enter-from,
.list-replace-leave-to {
  opacity: 0;
  transform: scale(0);
  transform-origin: center center;
}
.list-replace-leave-active {
  /* 防止页面抖动 */
  position: absolute;
}
</style>