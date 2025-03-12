<template>
  <div class="index">
    <div class="flex items-center gap-6">
      <div class="text-lg mr-auto">{{ isSelf ? '我的' : 'TA的' }}订阅</div>
       <miku-radio  v-model="sort" :options="sorts" class="text-sm" />
      <search-box v-model="keyword" @search="run(1)"/>
    </div>
    <el-divider class="my-1" />
    <async :loading="loading" :error="error" :empty="page?.empty" min-h="240px">
      <ul class="video-list">
        <template v-for="i in page.records" :key="i.id">
          <subscribe-item :data="i"/>
        </template>
      </ul>
      <miku-pagination v-model="pageNum" :page="page"/>
    </async>
  </div>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import SubscribeItem from './SubscribeItem.vue'
import bangumiApi from '@/apis/bangumi';
import { toInteger } from 'lodash';
const { userId, isSelf } = toRefs(useSpaceStore())
const sorts = [
  { label: '追番时间', value: 'subscribe_time', },
  { label: '追番人数', value: 'subscribe' },
]
const keyword = ref('')
const sort = useRouteQuery<BangumiParams['sort']>('sort', 'subscribe_time')
const pageNum = useRouteQuery('page', 1, { transform: toInteger })
const { page, run, loading, error } = usePage(search)
function search(pageNum: number) {
  return bangumiApi.search({
    subscribedUserId: userId.value,
    pageNum,
    pageSize: 12,
    title: keyword.value,
    sort: sort.value
  })
}
watch(sort, ()=>{
  pageNum.value = 1
  run(1)
})
watchImmediate(pageNum, ()=>run(pageNum.value))
</script>

<style scoped lang="scss">
.index {
  padding: 12px 20px;
  padding-bottom: 60px;
}
.video-list {
  padding: 16px 0;
  display: grid;
  column-gap: 16px;
  row-gap: 24px;
  grid-template-columns: repeat(3, 1fr);
}
.page-selector {
  display: flex; 
  justify-content: center;
  margin-top: 24px;
}
</style>