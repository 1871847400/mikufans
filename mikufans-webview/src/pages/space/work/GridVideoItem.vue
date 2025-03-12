<template>
  <div class="text-sm">
    <div class="banner">
      <async-image class="size-full" :res-id="video.bannerId" :href="'/video/'+video.sid" target="_blank"/>
      <div class="duration">{{ displayDuration(video.duration) }}</div>
    </div>
    <a class="title maxline-2 my-1 h-12 leading-6" :href="'/video/'+video.sid" target="_blank">{{ video.title }}</a>
    <div class="flex gap-2 text-xs">
      <span class="flex items-center gap-1 mr-auto">
        <i class="iconfont icon-bofang"></i>
        <span>{{ displayNumber(video.plays) }}</span>
      </span>
      <div>
        <slot name="datetime">{{ video.dynamic.publishTimeStr }}</slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { displayDuration, displayNumber } from '@/utils/common';
const props = defineProps({
  video: object<Video>().isRequired,
})
</script>

<style scoped lang="scss">
.title {
  cursor: pointer;
  &:hover {
    color: var(--blue0);
  }
}
.drop-item {
  width: 100%;
  height: 24px;
  line-height: 24px;
  text-align: center;
  user-select: none;
  cursor: pointer;
  &:hover {
    background-color: #f1f1f1;
  }
}
.banner {
  width: 100%;
  height: auto;
  aspect-ratio: 16 / 9;
  border-radius: 6px;
  overflow: hidden;
  position: relative;
  .duration {
    position: absolute;
    right: 8px;
    bottom: 6px;
    color: #fff;
    border-radius: 6px;
    padding: 1px 4px;
    background-color: #0000009c;
    font-size: 12px;
    pointer-events: none;
  }
}
</style>