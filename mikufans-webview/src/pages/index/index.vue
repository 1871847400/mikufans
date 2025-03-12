<template>
  <div class="index-page">
    <nav-bar/>
    <div class="flex flex-col py-4">
      <div class="w-[80%] mx-auto flex flex-col gap-4 min-h-[500px]" v-loading="isLoading">
        <template v-if="isReady">
          <template v-for="{ label, options, query } in state.filters">
            <index-list :label="label" :options="options" :query="query" />
          </template>
          <el-divider class="my-0" />
          <div class="flex gap-8">
            <template v-for="{ label, value } in state.sorts">
              <sort-button v-model="sort" :value="value" v-model:desc="desc">{{ label }}</sort-button>
            </template>
          </div>
          <video-list :type="type" :sort="sort" :desc="desc=='1'" />
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar/index.vue';
import IndexList from './IndexList.vue';
import VideoList from './VideoList.vue';
import bangumiApi from '@/apis/bangumi';
const route = useRoute()
const router = useRouter()
const sort = useRouteQuery<BangumiSort>('sort', 'subscribe', { mode: 'push' })
const desc = useRouteQuery('desc', '1', { mode: 'push' })
const type = useRouteQuery<VideoType>('type', 'ANIME')
const { state, isLoading, isReady, execute } = useAsyncState(()=>bangumiApi.indexes(type.value), null)
//切换类型时清空query参数,并重新搜索
watch(type, ()=>{
  execute()
  router.replace({
    path: route.path,
    query: {
      type: type.value
    }
  })
})
</script>

<style scoped lang="scss">
.index-page {
  min-height: 100vh;
}
</style>