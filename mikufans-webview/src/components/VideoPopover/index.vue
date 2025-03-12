<template>
  <div class="video-popover">
    <el-popover
      placement="bottom-start"
      trigger="hover"
      width="auto"
    >
      <div class="popover-content">
        <p>{{ video.title }}</p>
        <a :href="'/video/'+video.sid" target="_blank">
          <miku-image class="w-full" video :res-id="video.bannerId" />
        </a>
        <div class="video-details iconfont">
          <span class="icon-dianzan">{{ displayNumber(video.likeStatus.likes) }}</span>
          <span class="icon-dislike-line">{{ displayNumber(video.likeStatus.dislikes) }}</span>
          <span class="icon-coin">{{ displayNumber(video.coins) }}</span>
          <span class="icon-shoucang">{{ displayNumber(video.stars) }}</span>
          <span class="icon-zhuanfa">{{ displayNumber(video.shares) }}</span>
        </div>
      </div>
      <template #reference>
        <slot>
          <a class="video-title" :href="'/'+video.sid" target="_blank">{{ video.title }}</a>
        </slot>
      </template>
    </el-popover>
  </div>
</template>

<script setup lang="ts">
import { displayNumber } from '@/utils/common';
const props = defineProps<{
  video: Video
}>()
</script>

<style scoped lang="scss">
.video-popover {
  box-sizing: border-box;
  position: relative;
  &:hover {
    .popover {
      display: block;
    }
  }
}
.popover-content {
  padding: 8px 4px;
  width: 240px;
}
.popover-desc {
  font-size: 12px;
  .name {
    font-size: 16px;
    line-height: 28px;
  }
  .stats {
    white-space: nowrap;
    line-height: 22px;
    color: var(--text-color-2);
    span {
      margin-right: 4px;
    }
  }
  .sign {
    color: var(--text-color-3);
    padding-top: 12px;
    min-height: 16px;
  }
  .actions {
    margin-top: 8px;
  }
}
.video-title {
  color: var(--blue0);
  display: inline-block;
  white-space: nowrap;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
}
.video-details {
  display: flex;
  justify-content: space-around;
  & > span {
    &::before {
      padding-right: 4px;
    }
  }
}
</style>