<template>
  <div>
    <div class="flex items-center relative">
      <div class="text-lg">最近点赞的视频</div>
      <span v-if="page?.empty" class="grey2 abs-center">暂时什么也没有</span>
    </div>
    <el-divider class="my-2" />
    <div class="grid-view" v-if="page">
      <template v-for="i in page.records" :key="i.id">
        <grid-video-item :video="i" noedit>
          <template #datetime>
            <span class="flex items-center gap-1">
              <i class="iconfont icon-pinglun3"></i>
              <!-- <span>{{ displayNumber(i.commentArea.comments) }}</span> -->
            </span>
          </template>
        </grid-video-item>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import GridVideoItem from '../../work/GridVideoItem.vue';
import videoApi from '@/apis/video';
import { usePage } from '@/hooks/usePage';
import { displayNumber } from '@/utils/common';
const { userId } = toRefs(useSpaceStore())
const { page, loading, error, } = usePage(search, {
  immediate: [1]
})
function search(pageNum: number) {
  return videoApi.getLikes({
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
  margin: 16px 0;
  grid-template-columns: repeat(5, 1fr);
}
</style>