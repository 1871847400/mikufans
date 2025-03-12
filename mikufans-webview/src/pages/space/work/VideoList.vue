<template>
  <div class="px-4 flex-1">
    <div class="flex items-center gap-6 mb-4">
      <div class="text-lg mr-auto">全部作品</div>
      <div class="flex gap-4">
        <i class="iconfont icon-dasuolvetuliebiao cursor-pointer" :class="{grey2:layout!='grid'}" @click="nextLayout()"></i>
        <i class="iconfont icon-liebiao1 cursor-pointer mr-6" :class="{grey2:layout!='list'}" @click="nextLayout()"></i>
      </div>
      <simple-tab v-model:index="sort" :options="sorts" type="text" class="text-xs"/>
      <search-box v-model="keyword" placeholder="请输入视频标题" @search="run(1)" />
    </div>
    <Async :loading="loading" :error="error" :empty="page?.empty" empty-text="没有投稿过视频" min-h="300px">
      <div :class="{ 'grid-view': layout==='grid' }">
        <template v-for="video,index in page.records" :key="video.id">
          <template v-if="layout==='list'">
            <list-video-item :v="video"/>
            <el-divider v-if="index+1<page.records.length" class="my-4"/>
          </template>
          <template v-else>
            <grid-video-item :video="video"/>
          </template>
        </template>
      </div>
      <miku-pagination v-model="pageNum" :page="page" style="margin-top: 60px;" />
    </Async>
  </div>
</template>

<script setup lang="ts">
import videoApi from '@/apis/video';
import ListVideoItem from './ListVideoItem.vue'
import GridVideoItem from './GridVideoItem.vue';
import { usePage } from '@/hooks/usePage';
const props = defineProps<{
  userId: string,
}>()
const layout = useRouteQuery<'grid'|'list'>('layout', 'grid')
function nextLayout() {
  layout.value = layout.value=='grid' ? 'list' : 'grid'
}
const sorts : {label:string, value:VideoSearchSort}[] = [
  {
    label: '最新发布',
    value: 'TIME',
  },
  {
    label: '最多播放',
    value: 'PLAY'
  },
  {
    label: '最多收藏',
    value: 'STAR'
  },
]
const keyword = ref('')
const sort = ref(0)
const { page, run, loading, error } = usePage(search)
function search(pageNum: number) {
  return videoApi.search({
    userId: props.userId, 
    pageNum, 
    pageSize: 15, 
    sort: sorts[sort.value]?.value,
    keyword: keyword.value,
  })
}
const pageNum = useRouteQuery('page', 1)
watchImmediate(pageNum, ()=>{
  run(pageNum.value)
})
//当用户id,排序规则变化后重置页面
watch([()=>props.userId, sort], ()=>{
  pageNum.value = 1
  run(1)
})
</script>

<style scoped lang="scss">
.grid-view {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}
</style>