<template>
  <div>
    <div class="flex items-center relative justify-between">
      <div class="text-lg">{{ isSelf ? '我' : 'TA' }}的最新视频</div>
      <el-button link @click="changePage('work')" :icon="ArrowRightBold">查看全部视频</el-button>
      <span v-if="page?.empty" class="grey2 abs-center">暂时什么也没有</span>
    </div>
    <el-divider class="my-2" />
    <div class="grid-view" v-if="page">
      <template v-for="i in page.records" :key="i.id">
        <grid-video-item :video="i" noedit/>
      </template>
    </div>
  </div>
  <el-divider v-if="page?.empty===false" />
</template>

<script setup lang="ts">
import { ArrowRightBold } from '@element-plus/icons-vue'
import GridVideoItem from '../../work/GridVideoItem.vue';
import videoApi from '@/apis/video';
import { usePage } from '@/hooks/usePage';
const { isSelf, userId } = toRefs(useSpaceStore())
const { changePage } = useSpaceStore()
const { page, loading, error } = usePage(search, {
  immediate: [1]
})
function search(pageNum: number) {
  return videoApi.search({
    sort: 'TIME',
    userId: userId.value,
    searchCount: false,
    pageNum,
    pageSize: 5
  })
}
</script>

<style scoped lang="scss">
.grid-view {
  display: grid;
  gap: 24px;
  grid-template-columns: repeat(5, 1fr);
}
</style>