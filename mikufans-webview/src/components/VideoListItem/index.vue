<template>
  <div class="video-list-item">
    <div class="banner">
      <router-link :to="video.uri">
        <miku-image :res-id="video.bannerId" class="size-full"/>
      </router-link>
      <div class="duration">
        <slot name="duration">{{ displayDuration(video.duration) }}</slot>
      </div>
    </div>
    <div class="flex-1 flex flex-col justify-around">
      <router-link class="maxline-2 title" :to="video.uri" :title="video.title">{{ video.title }}</router-link>
      <router-link class="maxline-1 grey2 text-xs author" :to="video.user.uri">{{ video.user?.nickname || '未知昵称' }}</router-link>
      <slot name="stats">
        <div class="flex text-xs">
          <stats-icon icon="icon-24gl-playCircle" :count="video.plays" />
          <stats-icon icon="icon-danmushu" :count="video.danmus" />
        </div>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { displayDuration } from '@/utils/common';
defineProps<{ video: Video }>()
</script>

<style scoped lang="scss">
.video-list-item {
  display: flex;
  align-items: center;
  margin: 8px 0;
  transition: color .2s;
  font-size: 14px;
  .title {
    line-height: 1.5;
    height: calc(14px * 2 * 1.5);
    transition: color .2s;
  }
  .author::before {
    content: 'UP';
    scale: 0.6;
    display: inline-block;
    font-weight: 550;
    border: 2px solid var(--grey2);
    padding: 0 2px;
    margin-right: -2px;
    border-radius: 4px;
    transform: translateX(-4px);
  }
  &:hover .title {
    color: var(--blue0);
  }
  .banner {
    width: 40%;
    height: fit-content;
    white-space: nowrap;
    flex-shrink: 0;
    margin-right: 8px;
    position: relative;
    border-radius: 6px;
    overflow: hidden;
    .miku-image {
      width: 100%;
      height: auto;
      aspect-ratio: 16/9;
    }
    .duration {
      position: absolute;
      right: 4px;
      bottom: 4px;
      background-color: #0000009c;
      color: #fff;
      border-radius: 4px;
      padding: 0 2px;
      font-size: 12px;
      pointer-events: none;
    }
  }
}
</style>