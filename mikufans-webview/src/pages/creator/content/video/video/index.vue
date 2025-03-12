<template>
  <div>
    <miku-radio v-model="status" :options="options" class="px-8 my-4">
      <template v-slot="{ label, count }">
        <span>{{ label }}</span>
        <span class="inline-block min-w-4 text-right">{{ count || '0' }}</span>
      </template>
    </miku-radio>
    <Async :loading="loading" :empty="page?.empty" :error="error" min-h="400px">
      <ul class="px-8">
        <li v-for="i in page.records" :key="i.id">
          <video-item :video="i" @update="refresh" />
          <el-divider />
        </li>
      </ul>
      <miku-pagination class="my-4" v-model="pageNum" :page="page" />
    </Async>
  </div>
</template>

<script setup lang="ts">
import videoApi from '@/apis/video';
import VideoItem from './VideoItem.vue';
import { usePage } from '@/hooks/usePage';
const videoType = useRouteQuery<VideoType>('type', 'VIDEO')
const pageNum = useRouteQuery('page', 1, { transform: Number, mode: 'push' })
const status = useRouteQuery<UploadVideoSearchParams['status']>('status', 'ANY')
const options = computed(()=>{
  return [
    {
      label: '全部稿件',
      count: state.value?.ANY,
      value: 'ANY',
    },
    {
      label: '进行中',
      count: state.value?.DOING,
      value: 'DOING'
    },
    {
      label: '已通过',
      count: state.value?.SUCCESS,
      value: 'SUCCESS'
    },
    {
      label: '未通过',
      count: state.value?.FAIL,
      value: 'FAIL'
    },
  ]
})
const { execute, state } = useAsyncState(()=>videoApi.getUploadCount(videoType.value), null)
const { page, loading, reset, run, error } = usePage(search)
async function search(pageNum: number) {
  return videoApi.getUploadList({
    status: status.value,
    videoType: videoType.value,
    pageNum,
    pageSize: 10
  })
}
function refresh() {
  reset()
  run(1)
}
watch(videoType, ()=>{ 
  status.value = 'ANY'
  execute()
})
watch([status, videoType], refresh)
watchImmediate(pageNum, run)
</script>

<style scoped lang="scss">
</style>