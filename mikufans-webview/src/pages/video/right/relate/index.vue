<template>
  <div class="pb-4">
    <el-divider class="my-4" />
    <div class="mb-4">相关推荐</div>
    <template v-for="i in state" :key="i.id">
      <video-list-item :video="i"/>
    </template>
  </div>
</template>

<script setup lang="ts">
import videoApi from '@/apis/video';
const videoStore = useVideoStore()
//推荐的关联视频列表
const { state, execute } = useAsyncState(()=>videoApi.getRelatedList(videoStore.video?.id), [])
//检测到切换视频后重新推荐视频
watch(()=>videoStore.video, ()=>execute())
</script>

<style scoped lang="scss">
</style>