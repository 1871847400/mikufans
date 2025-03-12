<template>
  <div class="p-4">
    <div v-loading="loading">
      <el-table v-if="page?.records" :data="page.records" height="auto" style="width: 100%" border stripe @sort-change="onSort">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="sid" label="sid" width="80" />
        <el-table-column prop="title" label="标题" width="320" />
        <el-table-column sortable="custom" prop="score" label="热度" width="80" />
        <el-table-column prop="plays" label="播放" width="80" />
        <el-table-column prop="likeStatus.likes" label="点赞" width="80" />
        <el-table-column prop="likeStatus.dislikes" label="点踩" width="80" />
        <el-table-column prop="coins" label="投币" width="80" />
        <el-table-column prop="stars" label="收藏" width="80" />
        <el-table-column prop="danmus" label="弹幕" width="80" />
        <el-table-column prop="dynamic.commentArea.comments" label="评论" width="80" />
        <el-table-column prop="shares" label="分享" width="80" />
        <el-table-column prop="dynamic.publishTime" label="发布日期" width="120"/>
      </el-table>
      <miku-pagination v-model="pageNum" :page="page"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import videoApi from '@/apis/video';
import { usePage } from '@/hooks/usePage';
const pageNum = useRouteQuery('page', 1)
const sort = useRouteQuery<VideoSearchSort>('sort', 'TIME')
const desc = useRouteQuery<string>('desc', '')
const { page, loading, error, run } = usePage(search)
function search(pageNum: number) {
  return videoApi.getUploadList({
    status: 'ANY',
    sort: sort.value,
    asc: !desc.value,
    pageNum,
    pageSize: 12
  })
}
watchImmediate(pageNum, ()=>run(pageNum.value))
watch([sort, desc], ()=>{
  pageNum.value = 1
  run(1)
})
function onSort(data: {column: any, prop: string, order: string|null }) {
  // console.log(data.prop, data.order);
  if (data.order != 'ascending') {
    desc.value = '1'
  } else {
    desc.value = ''
  }
  //默认就是按创建时间排序
  if (data.order==null || data.prop=='publishDate') {
    sort.value = 'TIME'
  } else {
    sort.value = data.prop.toUpperCase() as VideoSearchSort
  }
}
</script>

<style scoped lang="scss">
</style>