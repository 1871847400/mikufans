<template>
  <div class="recommend-section">
    <div class="new-anime">
      <div class="flex items-center justify-between">
        <span class="text-2xl font-bold">猜你喜欢</span>
        <span class="new-anime-refresh" @click="execute()">
          <i class="iconfont font-bold">&#xe6a4;</i>
          <span>换一换</span>
        </span>
      </div>
      <el-skeleton class="new-anime-list" :loading="isLoading" animated :count="pageSize">
        <template #template>
          <div class="skeleton-item">
            <el-skeleton-item class="image" variant="image"/>
            <el-skeleton-item class="title" variant="p"/>
          </div>
        </template>
        <div class="new-anime-list">
          <anime-card v-for="i in state.records" :key="i.id" :bangumi="i"/>
        </div>
      </el-skeleton>
    </div>
  </div>
</template>

<script setup lang="ts">
import AnimeCard from './AnimeCard.vue';
import bangumiApi from '@/apis/bangumi';
const pageSize = 14
async function search() {
  return bangumiApi.search({
    videoType: 'ANIME',
    sort: 'random',
    pageSize,
    searchCount: false,
  })
}
const { state, isLoading, execute } = useAsyncState(search, null)
</script>

<style scoped lang="scss">
.new-anime-list {
  min-height: 300px;
  display: grid;
  padding: 12px 0;
  column-gap: 16px;
  row-gap: 32px;
  grid-template-columns: repeat(5, 1fr);
  grid-template-rows: repeat(2, auto);
}
@media screen and (min-width: 1100px) {
  .new-anime-list {
    grid-template-columns: repeat(6, 1fr);
  }
}
@media screen and (min-width: 1320px) {
  .new-anime-list {
    grid-template-columns: repeat(7, 1fr);
  }
}
.new-anime-refresh {
  padding: 2px 8px;
  user-select: none;
  cursor: pointer;
  border-radius: 12px;
  box-shadow: 0 0 2px rgba(0,0,0,.5);
  font-size: 14px;
  &:hover {
    background-color: var(--bg1);
  }
}
.skeleton-item {
  .image {
    width: 100%;
    height: auto;
    aspect-ratio: 0.74;
    border-radius: 12px;
  }
  .title {
    height: 22px;
    margin-top: 10px;
    border-radius: 8px;
  }
}
</style>