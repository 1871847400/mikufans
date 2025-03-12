<template>
  <li class="user-video-item">
    <div class="banner">
      <miku-image class="size-full" :res-id="v.bannerId">
        <a :href="'/video/'+v.sid" target="_blank"></a>
      </miku-image>
      <div class="duration">{{ displayDuration(v.duration) }}</div>
    </div>
    <div class="user-video-info">
      <a class="title" :href="'/video/'+v.sid" target="_blank">{{ v.title }}</a>
      <rich-text class="grey2 text-sm" html :content="v.intro" :rows="2" />
      <div class="bottom">
        <video-stats :video="v"/>
      </div>
    </div>
  </li>
</template>

<script setup lang="ts">
import { displayDuration } from '@/utils/common';
defineProps<{ v: Video }>()
</script>

<style scoped lang="scss">
.user-video-item {
  box-sizing: border-box;
  display: flex;
  align-items: center;
  height: 120px;
  position: relative;
  padding: 8px;
  border-radius: 6px;
  .banner {
    width: auto; //这里根据高度计算宽度
    height: 100%;
    aspect-ratio: 16 / 9;
    border-radius: 6px;
    overflow: hidden;
    position: relative;
    .duration {
      position: absolute;
      right: 8px;
      bottom: 8px;
      color: #fff;
      border-radius: 6px;
      padding: 1px 4px;
      background-color: #0000009c;
      font-size: 12px;
      pointer-events: none;
    }
  }
  &:hover {
    background-color: #eee;
  }
  .user-video-info {
    flex: 1;
    padding-left: 12px;
    height: 100%;
    display: flex;
    flex-direction: column;
    .title {
      display: block;
      font-size: 18px;
      line-height: 36px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    .bottom {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: auto;
    }
  }
}
</style>