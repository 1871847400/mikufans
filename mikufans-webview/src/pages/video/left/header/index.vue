<template>
  <div class="flex flex-col gap-1 mb-4">
    <div class="title">{{ video.title }}</div>
    <div class="stats">
      <span class="ico-num">
        <i class="iconfont icon-bofang"></i>
        <span>{{displayNumber(video.plays)}}</span>
      </span>
      <span class="ico-num">
        <i class="iconfont icon-danmushu"></i>
        <span>{{displayNumber(video.dynamic.commentArea.comments)}}</span>
      </span> 
      <span>{{ video.dynamic.publishTime }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { displayNumber } from '@/utils/common';
const { video } = toRefs(useVideoStore())
</script>

<style scoped lang="scss">
.title {
  font-weight: 500;
  font-size: 18px;
  line-height: 32px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.stats {
  color: var(--grey1);
  font-size: 13px;
  line-height: 18px;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 20px;
  .iconfont {
    font-size: 16px;
  }
  .ico-num {
    display: flex;
    align-items: center;
    gap: 4px;
  }
}
</style>