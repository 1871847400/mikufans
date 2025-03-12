<template>
  <div>
    <div class="flex pb-4">
      <a class="banner" :href="video?.uri" target="_blank">
        <miku-image class="w-[200px]" video :res-id="video?.bannerId"/>
        <span class="duration">{{ displayDuration(video?.duration) }}</span>
      </a>
      <div class="details">
        <a class="text-lg pb-1 maxline-2" :href="video?.uri" target="_blank">{{ video?.title || '视频已被删除' }}</a>
        <rich-text class="grey2 text-xs" :expand="false" :content="video?.intro" html :rows="2" />
        <div class="mt-auto">
          <stats-icon icon="icon-24gl-playCircle" :count="video?.plays" />
          <stats-icon icon="icon-danmushu" :count="video?.danmus" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { displayDuration } from '@/utils/common';
const props = defineProps<{ data: UserDynamic }>()
const video = toRef(()=>props.data.source as Video)
</script>

<style scoped lang="scss">
.banner {
  position: relative;
  .duration {
    position: absolute;
    background-color: #0000009b;
    color: #fff;
    right: 4px;
    bottom: 6px;
    padding: 2px 4px;
    border-radius: 4px;
    font-size: 12px;
    pointer-events: none;
  }
}
.details {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 4px 12px;
  border: 1px solid var(--bg2);
  border-left: none;
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
}
</style>