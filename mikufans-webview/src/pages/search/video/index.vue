<template>
  <div>
    <search-sort :options="options"/>
    <slot></slot>
    <div v-if="loading" class="video-list">
      <video-item-skeleton v-for="i in pageSize" />
    </div>
    <Async v-else :error="error" :empty="page?.empty" empty-text="没有找到相关视频">
      <div class="video-list" v-if="page">
        <template v-for="i in page.records" :key="i.id">
          <video-item :video="i" />
        </template>
      </div>
      <miku-pagination v-model="pageNum" :page="page" />
    </Async>
  </div>
</template>

<script setup lang="ts">
import VideoItemSkeleton from '@/pages/home/main/VideoItemSkeleton.vue';
import VideoItem from '@/pages/home/VideoItem.vue';
import SearchSort from '../sort/SearchSort.vue';
import videoApi from '@/apis/video';
import { usePage } from '@/hooks/usePage';
import videoSearchApi from '@/apis/video/search';
import { pick } from 'lodash';
const props = defineProps<{ keyword: string }>()
const pageSize = 20
const pageNum = useRouteQuery('page', 1)
const route = useRoute()
const { state: options } = useAsyncState(videoSearchApi.getOptions(), [])
const { page, loading, run, error } = usePage(search)
function search(pageNum: number) {
  return videoApi.search({
    type: 'VIDEO',
    keyword: props.keyword,
    pageNum,
    pageSize: 20,
    ...pick(route.query, options.value.map(a=>a.query))
  })
}
watchImmediate(()=>route.query, ()=>{
  run(pageNum.value)
})
</script>

<style scoped lang="scss">
.video-list {
  padding: 16px 0;
  display: grid;
  /* background-color: #fff; */
  column-gap: 16px;
  row-gap: 24px;
  grid-template-columns: repeat(4, 1fr);
  grid-auto-rows: min-content;
}
@media screen and (min-width: 1320px) {
  .video-list {
    grid-template-columns: repeat(5, 1fr);
  }
}
</style>