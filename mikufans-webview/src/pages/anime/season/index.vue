<template>
  <div>
    <div class="flex items-center gap-4 mb-3">
      <div class="flex items-center gap-1">
        <img class="size-8" :src="Clock" alt="">
        <span class="text-2xl font-bold grey0">季度新番</span>
      </div>
      <el-segmented v-model="week" :options="weeks" />
    </div>
    <div v-if="page" class="relative">
      <div ref="container" class="anime-list" :class="{full:page.records.length >= pageSize}">
        <template v-for="i in page.records.slice(0, pageSize)" :key="i.id">
          <anime-item :data="i" :page-size="pageSize" />
        </template>
      </div>
      <prev-and-next :page="page.current" :done="done" @next="execute()" @prev="execute(false)" offset="20px" />
    </div>
  </div>
</template>

<script setup lang="ts">
import Clock from '@/assets/images/clock.png'
import bangumiApi from '@/apis/bangumi';
import { usePage } from '@/hooks/usePage';
import AnimeItem from './AnimeItem.vue';
import gsap from 'gsap';
import PrevAndNext from '../PrevAndNext.vue';
const week = ref(null)
const weeks = [
  { label: '全部', value: null, },
  { label: '周一', value: 1, },
  { label: '周二', value: 2, },
  { label: '周三', value: 3, },
  { label: '周四', value: 4, },
  { label: '周五', value: 5, },
  { label: '周六', value: 6, },
  { label: '周日', value: 7, },
]
const { width } = useWindowSize()
const pageSize = computed(()=>width.value<=1535?5:6)
const { page, next, prev, done, reset } = usePage(search)
function search(pageNum: number) {
  return bangumiApi.search({
    pageNum,
    pageSize: pageSize.value,
    week: week.value,
  })
}
const container = useTemplateRef('container')
function execute(right = true) {
  gsap.to(container.value, {
    translateX: right ? '-60px' : '60px',
    duration: .3,
    opacity: 0
  }).eventCallback('onComplete', async ()=>{
    await (right ? next() : prev())
    gsap.fromTo(container.value, {
      translateX: right ? '60px' : '-60px',
      opacity: 0
    }, {
      translateX: '0',
      opacity: 1,
      duration: .3,
    })
  })
}
watchImmediate(week, ()=>{
  reset()
  next()
})
</script>

<style scoped lang="scss">
.el-select :deep(.el-select__wrapper) {
  box-shadow: none;
}
.anime-list {
  display: flex;
  overflow: hidden;
  /* 撑起显示标题的高度 */
  padding-bottom: 45px;
  gap: 16px;
  //子节点默认显示的宽度
  --width: calc((100% - v-bind('pageSize - 1') * 16px) / v-bind(pageSize));
  &.full {
    justify-content: center;
  }
}
</style>