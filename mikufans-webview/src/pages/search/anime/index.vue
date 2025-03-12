<template>
  <div>
    <Async :loading="loading" :empty="page?.records?.length==0" :error="error" min-h="200px">
      <div class="anime-list" v-if="page">
        <template v-for="v in page.records" :key="v.id">
          <anime-search-item :video="v"/>
        </template>
      </div>
      <miku-pagination v-model="pageNum" :page="page" />
    </Async>
  </div>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import videoApi from '@/apis/video';
import AnimeSearchItem from './AnimeSearchItem.vue';
const keyword = useRouteQuery('kw', '')
const pageNum = useRouteQuery('page', 1)
const { page, loading, run, error } = usePage(search)
function search(pageNum: number) {
  return videoApi.search({
    pageNum,
    pageSize: 20,
    type: 'ANIME',
    keyword: keyword.value,
  })
}
watchImmediate([keyword, pageNum], ()=>run(pageNum.value))
</script>

<style scoped lang="scss">
.anime-list {
  display: flex;
  flex-wrap: wrap;
  padding: 12px 0;
  .anime-search-item {
    flex: 50% 0 0;
  }
}
</style>