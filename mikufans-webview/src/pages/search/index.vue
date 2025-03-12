<template>
  <div class="min-h-[100vh]">
    <nav-bar :search="false" />
    <search-header />
    <main class="px-[128px] xl:px-[64px] pb-10">
      <thumb-tab v-model="searchType" :options="searchTypes" class="h-[60px]" :thumb-style="{width:'38px'}">
        <template v-slot="{ label, count }">
          <div class="flex items-center gap-1 pr-4">
            <span>{{ label }}</span>
            <span v-if="isNumber(count)" class="bg1 rounded px-1 text-xs">{{ count > 99 ? '99+' : count }}</span>
          </div>
        </template>
      </thumb-tab>
      <el-divider class="mt-0 mb-4"/>
      <component :is="active.component" :state="state" :keyword="keyword" />
    </main>
  </div>
</template>

<script setup lang="ts">
import SearchHeader from './header/index.vue'
import AllIndex from './all/index.vue'
import VideoIndex from './video/index.vue';
import UserIndex from './user/index.vue';
import AnimeIndex from './anime/index.vue';
import MovieIndex from './movie/index.vue'
import { searchAll } from '@/apis/search';
import { isNumber } from 'lodash';
const route = useRoute()
//搜索类型
const searchType = useRouteParams<string>('type', '')
//搜索关键词
const keyword = useRouteQuery('kw', '')
const { state, execute } = useAsyncState(()=>searchAll(keyword.value), null, { resetOnExecute: false })
watch(keyword, ()=>execute())
//活动项
const active = computed(()=>searchTypes.value.find(a=>a.value==searchType.value))
const searchTypes = computed(()=>[
  { label: '综合', value: 'all', component: markRaw(AllIndex), count: null },
  { label: '视频', value: 'video', component: markRaw(VideoIndex), count: state.value?.video?.total },
  { label: '番剧', value: 'anime', component: markRaw(AnimeIndex), count: state.value?.anime?.total },
  { label: '电影', value: 'movie', component: markRaw(MovieIndex), count: state.value?.movie?.total },
  { label: '用户', value: 'user', component: markRaw(UserIndex), count: state.value?.user?.total },
])
//切换类型时删除其它筛选
watch(searchType, ()=>{
  for (const key in route.query) {
    if (key != 'kw') {
      delete route.query[key]
    }
  }
})
</script>

<style scoped lang="scss">
</style>