<template>
  <el-scrollbar max-height="520px">
    <div v-if="!loading && list.length===0" class="text-center py-6">
      <span>没有搜索历史</span>
    </div>
    <div v-else v-infinite-scroll="next" :infinite-scroll-disabled="loading" :infinite-scroll-distance="30">
      <template v-for="group in groupList">
        <div class="my-2 px-4">{{ displayDateRange(group[0].lastWatchTime) }}</div>
        <template v-for="i in group" :key="i.id">
          <video-list-item v-if="videos[i.id]" :video="videos[i.id]">
            <template #duration>
              {{ displayDuration(i.watchPos) }}/{{ displayDuration(videos[i.id].duration) }}
            </template>
            <template #stats>
              <div class="text-xs flex items-center gap-[2px]">
                <i v-if="i.device===2" class="iconfont icon-shouji text-sm"></i>
                <i v-else class="iconfont icon-diannao"></i>
                <span>{{ i.lastWatchTimeStr }}</span>
              </div>
            </template>
          </video-list-item>
        </template>
      </template>
      <div v-if="loading || error" class="text-center py-6">
        <template v-if="loading">加载中...</template>
        <template v-else>加载失败！</template>
      </div>
    </div>
  </el-scrollbar>
</template>

<script setup lang="ts">
import videoHistoryApi from '@/apis/video/history';
import { usePage } from '@/hooks/usePage';
import videoApi from '@/apis/video';
import { displayDuration } from '@/utils/common';
import { groupBy } from 'lodash';
import { displayDateRange } from '@/utils/datetime';
function search(pageNum: number) {
  return videoHistoryApi.search({
    pageNum,
    pageSize: 6,
  })
}
const videos = shallowReactive<Record<string, Video>>({})
const { list, next, loading, error } = usePage(search, {
  immediate: [1],
  async onAppend(item) {
    videos[item.id] = await videoApi.getById(item.videoId)
  },
})
const groupList = computed(()=>{
  return groupBy(list.value, a=>displayDateRange(a.lastWatchTime))
})
</script>

<style scoped lang="scss">
.video-list-item {
  margin: 0;
  padding: 8px 16px;
  transition: all .2s;
  &:hover {
    background-color: var(--bg2);
  }
}
</style>