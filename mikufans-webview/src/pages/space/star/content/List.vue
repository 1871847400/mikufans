<template>
  <async v-if="page" :loading="loading" :error="error" :empty="page?.empty" min-h="300px">
    <div class="grid gap-4 grid-cols-5 xl:grid-cols-4 lg:gird-cols-3">
      <template v-for="i in page.records" :key="i.id">
        <list-item :data="i" @delete="cancelStar"/>
      </template>
    </div>
    <div class="flex justify-end mt-10 px-2">
      <el-pagination
        hide-on-single-page
        layout="prev, pager, next"
        background
        :current-page="page.current"
        :total="page.total"
        :page-size="page.size"
        @current-change="run"
      />
    </div>
  </async>
</template>

<script setup lang="ts">
import videoStarApi from '@/apis/video/star';
import { usePage } from '@/hooks/usePage';
import ListItem from './ListItem.vue';
import { useStore } from '../store';
const props = defineProps<{ sort: any, keyword: string  }>()
const { selectStar } = useStore()
const { page, reset, run, next, loading, error } = usePage(search, {
  compare(a, b) {
    return a.id === b.id
  },
})
function search(pageNum: number) {
  return videoStarApi.search({
    starId: selectStar.value?.id,
    title: props.keyword,
    pageNum,
    pageSize: 20,
    dir: props.sort==1?'ASC':'DESC',
  })
}
//更换收藏夹后刷新内容到第一页
//更换搜索内容
//更换排序
watchImmediate([selectStar, ()=>props.keyword, ()=>props.sort], ()=>{
  if (selectStar.value) {
    reset()
    next()
  }
})
//当用户取消收藏后刷新当前收藏列表
function cancelStar() {
  let pageNum = page.value.current
  const length = page.value.records.length
  if (length <= 1) {
    pageNum = Math.max(pageNum - 1, 1)
  }
  run(pageNum)
  message.info('已取消收藏')
}
</script>

<style scoped lang="scss">
</style>