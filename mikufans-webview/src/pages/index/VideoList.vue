<template>
  <div>
    <Async :loading="loading" :empty="page?.empty" :error="error" min-h="300px">
      <ul class="grid grid-cols-6 xl:grid-cols-5 gap-4 mb-16">
        <li v-for="i in page.records" :key="i.id">
          <Bangumi :video="i.video" :bangumi="i">
            <template #tip v-if="sort=='play'">
              {{ i.video.plays }}次播放
            </template>
            <template #tip v-else-if="sort=='premiere'">
              {{ i.premiere }}开播
            </template>
            <template #tip v-else-if="sort=='subscribe'">
              {{ i.subscribe }}人追番
            </template>
          </Bangumi>
        </li>
      </ul>
      <miku-pagination v-model="pageNum" :page="page" />
    </Async>
  </div>
</template>

<script setup lang="ts">
import bangumiApi from '@/apis/bangumi';
import { usePage } from '@/hooks/usePage';
import { getIntQuery, getStrQuery } from '@/utils/route';
const props = defineProps<{ type: VideoType, sort: BangumiSort, desc: boolean }>()
const route = useRoute()
const { page, loading, error, run } = usePage(search)
function search(pageNum: number) {
  return bangumiApi.search({
    videoType: props.type,
    region: getStrQuery('region'),
    season: getIntQuery('season'),
    sort: props.sort,
    asc: !props.desc,
    status: getIntQuery('status'),
    style: getStrQuery('style'),
    week: getIntQuery('week'),
    year: getIntQuery('year'),
    pageNum,
    pageSize: 24,
  })
}
const pageNum = useRouteQuery('page', 1, { mode: 'push' })
// watchImmediate(pageNum, ()=>)
watchImmediate(()=>route.query, ()=>{
  run(pageNum.value)
})
</script>

<style scoped lang="scss">

</style>