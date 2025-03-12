<template>
  <router-link 
    :class="listType?'video-part-item':'video-part-grid-item'"
    :title="part.partName" 
    :to="part.uri"
    :data-active="active"
  >
    <template v-if="listType">
      <div class="banner">
        <miku-image :res-id="part.bannerId" video class="size-full pointer-events-none" />
        <span class="duration">{{ displayDuration(part.resource?.duration) }}</span>
      </div>
      <div class="detail">
        <div class="flex items-center pb-2">
          <img class="playing" :src="Playing" alt="">
          <div class="part-num">
            <template v-if="video.type==='ANIME'">
              第<span class="px-[1px]">{{ part.sort }}</span>集
            </template>
            <template v-else>P{{ part.sort }}</template>
          </div>
        </div>
        <div class="maxline-1">{{ part.partName }}</div>
      </div>
    </template>
    <span v-else>{{ part.sort }}</span>
  </router-link>
</template>

<script setup lang="ts">
import Playing from '@/assets/images/playing.png'
import { displayDuration } from '@/utils/common';
const props = defineProps<{
  video: Video
  part: VideoPart
  active: boolean,
  listType: boolean
}>()
</script>

<style scoped lang="scss">
.video-part-item {
  padding: 6px;
  margin-right: 12px; //避开滚动条
  margin-top: 8px;
  /* height: 36px; */
  display: flex;
  align-items: center;
  cursor: pointer;
  white-space: nowrap;
  user-select: none;
  border-radius: 4px;
  /* transition: all 0.2s; */
  &:hover {
    background-color: var(--bg0);
  }
  &[data-active=true] {
    background-color: var(--bg0);
    .detail {
      color: var(--blue0);
      img.playing {
        display: block;
      }
    }
  }
  .banner {
    width: 100px;
    height: auto;
    aspect-ratio: 16/9;
    position: relative;
    border-radius: 4px;
    overflow: hidden;
    margin-right: 12px;
    flex-shrink: 0;
    .duration {
      position: absolute;
      font-size: 12px;
      right: 4px;
      bottom: 4px;
      color: #fff;
      background-color: #0000006a;
      padding: 0 4px;
      border-radius: 4px;
      pointer-events: none;
    }
  }
  .detail {
    img.playing {
      width: 14px;
      height: 14px;
      margin-right: 8px;
      display: none;
    }
    .part-num {
      width: calc(100% - 14px - 8px); //减去图片占的宽度
    }
  }
}
.video-part-grid-item {
  user-select: none;
  border-radius: 6px;
  cursor: pointer;
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  background-color: var(--bg0);
  border: 1px solid var(--bg0);
  /* transition: all 0.2s; */
  &:hover:not([data-active=true]) {
    background-color: #dff6fd;
    border-color: var(--blue0);
  }
  &[data-active=true] {
    background-color: var(--blue0);
    color: #fff;
  }
}
</style>