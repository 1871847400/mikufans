<template>
  <a ref="el" class="video-item" :href="href" target="_blank" draggable="false">
    <slot name="prepend"></slot>
    <div class="video-banner" ref="banner">
      <keep-alive>
        <mini-video-player v-if="hover" :data="video" @timeupdate="currentTime=$event"/>
        <template v-else>
          <miku-image class="size-full" video :res-id="video.bannerId" draggable="false" />
          <div class="video-stats">
            <span>
              <i class="iconfont">&#xea6e;</i>
              <span>{{ displayNumber(video.plays) }}</span>
            </span>
            <span>
              <i class="iconfont">&#xe665;</i>
              <span>{{ displayNumber(video.danmus) }}</span>
            </span>
            <span class="ml-auto">{{ displayDuration(video.duration) }}</span>
          </div>
        </template>
      </keep-alive>
    </div>
    <div class="footer">
      <user-avatar v-if="mode==='default'" :user="video.user" size="40px"/>
      <div class="footer-right">
        <div class="video-title maxline-2" :title="video.title" v-html="video.highlightTitle||video.title"></div>
        <a class="video-upload-info" :href="video.user.uri" target="_blank" draggable="false">
          <user-avatar v-if="mode==='inline'" :user="video.user" size="24px" class="mr-1"/>
          <div class="flex-1 maxline-1" :title="video.user.nickname">{{ video.user.nickname }}</div>
          <span class="upload-date">{{ video.dynamic.publishTimeStr }}</span>
        </a>
        <slot name="footer"></slot>
      </div>
    </div>
    <slot name="append"></slot>
  </a>
</template>

<script setup lang="ts">
import { displayNumber, displayDuration } from '@/utils/common';
const props = defineProps({
  video: object<Video>().isRequired,
  mode: string<'default'|'inline'>().def('default')
})
const el = useTemplateRef('el')
const hover = useElementHover(el, { delayEnter: 200, delayLeave: 200 })
const currentTime = ref(0)
const href = computed(()=>{
  let url = '/video/' + props.video.sid
  if (currentTime.value >= 3) {
    url += '#time' + currentTime.value
  }
  return url
})
</script>

<style scoped lang="scss">
.video-item {
  cursor: pointer;
}
.video-banner {
  flex-shrink: 0;
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 16 / 9;
  box-shadow: 0 0 2px rgba(0, 0, 0, .2);
  &:hover .video-stats {
    opacity: 0;
  }
}
.footer {
  margin-top: 6px;
  display: flex;
  .footer-right {
    flex: 1;
    padding: 0 6px;
    &>*:hover {
      transition: color .2s linear;
      color: #42b2ec;
    }
  }
  .video-title {
    height: 40px;
    line-height: 20px;
    font-size: 15px;
    font-weight: 500;
    margin-bottom: 8px;
  }
}
.video-upload-info {
  width: 100%;
  display: flex;
  height: 18px;
  line-height: 18px;
  align-items: center;
  color: var(--grey1);
  justify-content: space-between;
  font-size: 13px;
}
.video-stats {
  display: flex;
  align-items: center;
  gap: 12px;
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2;
  padding: 8px 12px;
  padding-top: 20px; //让阴影范围更大
  background: var(--img-shadow);
  color: #fff;
  font-size: 14px;
  pointer-events: none;
  transition: opacity .3s;
  span {
    white-space: nowrap;
    max-width: 33%;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .iconfont {
    margin-right: 4px;
  }
}
</style>