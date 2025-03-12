<template>
  <div class="video-stats">
    <stats-icon icon="icon-dianzan" :count="video.likeStatus.likes" />
    <stats-icon icon="icon-dislike-line" :count="video.likeStatus.dislikes" />
    <stats-icon icon="icon-shoucang3" :count="video.stars" />
    <stats-icon icon="icon-24gl-playCircle" :count="video.plays" />
    <stats-icon icon="icon-pinglun2" :count="video.dynamic.commentArea.comments" />
    <stats-icon icon="icon-danmushu" :count="video.danmus" />
    <stats-icon icon="icon-zhuanfa" :count="video.shares" />
    <span class="ml-4">{{ video.dynamic.publishTimeStr  }}</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  video: Video
}>()
</script>

<style scoped lang="scss">
.video-stats {
  display: flex;
  color: var(--grey2);
  font-size: 14px;
  align-items: center;
  overflow: hidden;
  white-space: nowrap;
  padding: 4px 0;
}
</style>