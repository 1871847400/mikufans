<template>
  <div>
    <Async :loading="loading" :empty="page?.records?.length==0" :error="error" min-h="200px">
      <div class="movie-list" v-if="page">
        <template v-for="v in page.records" :key="v.id">
          <movie-search-item :video="v"/>
        </template>
      </div>
      <miku-pagination v-model="pageNum" :page="page" />
    </Async>
  </div>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import videoApi from '@/apis/video';
import MovieSearchItem from './MovieSearchItem.vue';
const keyword = useRouteQuery('kw', '')
const pageNum = useRouteQuery('page', 1)
const { page, loading, run, error } = usePage(search)
function search(pageNum: number) {
  return videoApi.search({
    pageNum,
    pageSize: 20,
    type: 'MOVIE',
    keyword: keyword.value,
  })
}
watchImmediate([keyword, pageNum], ()=>run(pageNum.value))
</script>

<style scoped lang="scss">
.movie-list {
  display: flex;
  flex-wrap: wrap;
  padding: 12px 0;
  .movie-search-item {
    flex: 50% 0 0;
  }
}
</style>