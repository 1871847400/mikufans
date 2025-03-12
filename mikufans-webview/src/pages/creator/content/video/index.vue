<template>
  <div>
    <thumb-tab v-model="videoType" :options="videoTypes" class="mx-8" style="gap: 32px; height: 60px;" />
    <el-divider class="mb-2 mt-0" />
    <video-index />
  </div>
</template>

<script setup lang="ts">
import VideoIndex from './video/index.vue'
const videoType = useRouteQuery<VideoType>('type', 'VIDEO')
const videoTypes = [
  {
    label: '视频管理',
    value: 'VIDEO',
  },
  {
    label: '番剧管理',
    value: 'ANIME',
  },
  {
    label: '电影管理',
    value: 'MOVIE'
  }
]
</script>

<style scoped lang="scss">
</style>