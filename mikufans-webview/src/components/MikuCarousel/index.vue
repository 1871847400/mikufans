<template>
  <div class="miku-carousel">
    <div ref="listRef" class="carousel-list">
      <div v-for="i in __list" class="carousel-item">
        <slot v-bind="i"></slot>
      </div>
    </div>
    <slot name="footer" v-bind="list[index]"></slot>
    <div class="carousel-arrow">
      <div class="arrow-left" @click="moveLeft">
        <i class="iconfont icon-xiangzuojiantou"></i>
      </div>
      <div class="arrow-right rotate-180" @click="moveRight">
        <i class="iconfont icon-xiangzuojiantou"></i>
      </div>
    </div>
    <div class="carousel-dot">
      <pac-man 
        v-for="i in count" 
        :active="index==i-1" 
        :dir="index>lastIndex?'right':'left'" 
        @click="moveTo(i-1)" 
        color="#ffffff4d" 
        active-color="#fff"
      />
    </div>
  </div>
</template>

<script setup lang="ts" generic="T extends object">
import { cloneDeep } from 'lodash';
const props = defineProps({
  list: array<T>().def([]),
  interval: number().def(4000)
})
const __list = shallowRef<T[]>([])
const count = computed(()=>props.list.length)
const listRef = useTemplateRef('listRef')
function init() {
  __list.value = [
    cloneDeep(props.list[count.value - 1]),
    ...props.list,
    cloneDeep(props.list[0]),
  ]
  // //复制第一个元素到最后
  // const first = listRef.value.firstElementChild.cloneNode(true)
  // //复制最后一个元素到最前面
  // const last = listRef.value.lastElementChild.cloneNode(true)
  // listRef.value.appendChild(first)
  // listRef.value.insertBefore(last, listRef.value.firstElementChild)
}
const index = ref(0)
//记录上次位置,便于做吃豆人方向
const lastIndex = ref(-1)
function moveTo(idx: number) {
  resume() //重置定时任务
  listRef.value.style.transition = '.3s'
  listRef.value.style.transform = `translateX(${-idx*100}%)`
  lastIndex.value = index.value
  index.value = idx
}
function moveLeft() {
  if (index.value === 0) {
    listRef.value.style.transition = 'none'
    listRef.value.style.transform = `translateX(${-count.value*100}%)`
    listRef.value.clientHeight //立即重新渲染
    moveTo(count.value - 1)
    lastIndex.value = count.value
  } else {
    moveTo(index.value - 1)
  }
}
function moveRight() {
  if (index.value === count.value - 1) {
    listRef.value.style.transition = 'none'
    listRef.value.style.transform = 'translateX(100%)'
    listRef.value.clientHeight
    moveTo(0)
    lastIndex.value = -1
  } else {
    moveTo(index.value + 1)
  }
}
onMounted(()=>{
  watchImmediate(()=>props.list, init)
})
const isHover = useElementHover(listRef)
const { pause, resume } = useIntervalFn(moveRight, toRef(props, 'interval'))
watch(isHover, hover=>{
  hover ? pause() : resume()
})
defineExpose({
  index: readonly(index),
  pause,
  resume,
  moveTo,
})
</script>

<style scoped lang="scss">
.miku-carousel {
  width: 100%;
  height: auto;
  overflow: hidden;
  position: relative;
  border-radius: 8px;
}
.carousel-list {
  width: 100%;
  height: auto;
  display: flex;
  position: relative;
  .carousel-item {
    width: 100%;
    height: auto;
    flex-shrink: 0;
  }
  //将第一张复制品设置为绝对定位
  .carousel-item:nth-child(1) {
    position: absolute;
    transform: translateX(-100%);
  }
}
.carousel-arrow {
  position: absolute;
  display: flex;
  gap: 10px;
  right: 3%;
  bottom: 12%;
  z-index: 2;
  .arrow-left, .arrow-right {
    width: 28px;
    height: 28px;
    line-height: 28px;
    color: #fff;
    user-select: none;
    text-align: center;
    font-size: 13px;
    background: rgba(255,255,255,.3);
    border-radius: 8px;
    cursor: pointer;
    transition: background .3s;
    &:hover {
      background: rgba(255,255,255,.4);
    }
  }
}
.carousel-dot {
  left: 3%;
  bottom: 6%;
  display: flex;
  position: absolute;
  column-gap: 10px;
  z-index: 1;
  .pac-man {
    cursor: pointer;
    width: 12px;
  }
  .pac-man.active {
    scale: 1.4;
  }
}
</style>