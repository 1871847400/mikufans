<template>
  <section class="video-list" v-infinite-scroll="next" :infinite-scroll-disabled="loading">
    <template v-for="i,k in list" :key="i.id">
      <video-item :video="i" v-if="defer(k)"/>
    </template>
    <template v-for="i in pageSize">
      <video-item-skeleton :loading="loading" />
    </template>
  </section>
  <div class="footer">
    <div v-if="done">找不到更多视频了...</div>
  </div>
</template>

<script setup lang="ts">
import videoApi from '@/apis/video';
import VideoItem from '../VideoItem.vue';
import { useDefer } from '@/utils/defer';
import { usePage } from '@/hooks/usePage';
import VideoItemSkeleton from './VideoItemSkeleton.vue';
const { width } = useWindowSize()
const pageSize = computed(()=>width.value<1300?4:5)
//优化白屏时间
const defer = useDefer()
const { loading, list, next, done } = usePage(request)
function request(pageNum: number) {
  return videoApi.search({
    pageNum,
    pageSize: pageSize.value,
    sort: 'TIME',
  })
}
</script>

<style scoped lang="scss">
.video-list {
  user-select: none;
  display: grid;
  column-gap: 24px;
  row-gap: 36px;
  grid-template-columns: repeat(v-bind(pageSize), 1fr);
}
.footer {
  text-align: center; 
  font-size: 12px; 
  color: #676767; 
  padding: 32px 0;
}
</style>