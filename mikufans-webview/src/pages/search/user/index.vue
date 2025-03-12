<template>
  <div>
    <search-sort :options="options"/>
    <Async :loading="loading" :empty="page?.empty" :error="error" min-h="240px">
      <div class="user-list" v-if="page">
        <template v-for="u in page.records" :key="u.id">
          <user-search-item :user="u"/>
        </template>
      </div>
      <miku-pagination v-model="pageNum" :page="page" />
    </Async>
  </div>
</template>

<script setup lang="ts">
import UserSearchItem from './UserSearchItem.vue';
import userApi from '@/apis/user';
import SearchSort from '../sort/SearchSort.vue';
import { usePage } from '@/hooks/usePage';
import { pick } from 'lodash';
const props = defineProps<{ keyword: string }>()
const pageNum = useRouteQuery('page', 1)
const route = useRoute()
const { state: options } = useAsyncState(userApi.getSearchOptions(), [])
const { page, loading, error, run } = usePage(search)
function search(pageNum: number) {
  return userApi.search({
    nickname: props.keyword,
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
.user-list {
  display: flex;
  flex-wrap: wrap;
  row-gap: 16px;
  padding: 16px 0;
  .user-search-item {
    flex: 50% 0 0;
    margin-bottom: 24px;
  }
}
</style>