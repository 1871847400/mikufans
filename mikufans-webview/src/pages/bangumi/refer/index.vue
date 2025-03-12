<template>
  <div class="bg0 p-4 rounded">
    <div class="pb-4">
      <span class="font-bold">更多推荐</span>
    </div>
    <div class="grid grid-cols-7 xl:grid-cols-6 gap-4">
      <template v-for="i in state" :key="i.id">
        <Bangumi :video="i" />
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import videoApi from '@/apis/video';
const { video } = useBangumiStore()
const { state } = useAsyncState(videoApi.getRelatedList(video.id, 14), [])
</script>

<style scoped lang="scss">
</style>