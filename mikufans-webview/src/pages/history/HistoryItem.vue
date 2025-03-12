<template>
  <li class="history-item">
    <div ref="anchorRef" class="timetext" @click="anchor">{{ timetext }}</div>
    <div class="time">{{ lastWatchTimeStr }}</div>
    <router-link :to="uri" target="_blank">
      <miku-image class="w-40" video :res-id="video.bannerId"/>
    </router-link>
    <div class="video-details">
      <div class="left">
        <router-link 
          class="video-title" 
          :to="uri"
          target="_blank"
          v-html="highlighted||video.title"
        />
        <div class="video-intro maxline-2">{{ video.intro }}</div>
        <div class="watch-info">
          <i v-if="device===2" class="iconfont icon-shouji text-sm"></i>
          <i v-else class="iconfont icon-diannao text-xl"></i>
          <span class="w-1"></span>
          <span v-if="watchPos<1000">刚开始看</span>
          <span v-else>
            <span class="mr-1">看到</span>
            <span v-if="video.bangumi">{{ part.partName }}</span>
            <span>{{ displayDuration(watchPos) }}</span>
          </span>
          <span class="flex items-center gap-2 ml-auto">
            <user-avatar :user="video.user" size="24px"/>
            <router-link class="w-20 truncate" :to="video.user.uri" target="_blank">{{ video.user.nickname }}</router-link>
          </span>
        </div>
      </div>
      <div class="right">
        <i class="iconfont del-icon" @click="remove()" title="删除播放记录">&#xe645;</i>
      </div>
    </div>
  </li>
</template>

<script setup lang="ts">
import videoHistoryApi from '@/apis/video/history';
import { displayDuration } from '@/utils/common';
import { displayDateRange } from '@/utils/datetime';
import { useDocumentEvent } from '@/utils/event';
//注意vue不能使用外部type当props
const props = defineProps<{
  data: VideoWatchHistory
  prev?: VideoWatchHistory
}>()
const { id, video, part, highlighted, lastWatchTime, device, watchPos, lastWatchTimeStr, uri } = props.data
const emits = defineEmits(['remove'])
const timetext= computed(()=>{
  const text = displayDateRange(lastWatchTime)
  let text2 = ''
  if (props.prev) {
    text2 = displayDateRange(props.prev.lastWatchTime)
  }
  return text != text2 ? text : ''
})
function anchor(e: PointerEvent) {
  const ele = e.target as HTMLElement
  ele.parentElement.scrollIntoView({
    behavior: 'smooth',
    block: 'center', //元素对其屏幕顶端,中部,底部
    inline: 'start',
  })
}
async function remove() {
  await videoHistoryApi.remove(id)
  emits('remove')
}
let scrollTop = -1
const anchorRef = ref<HTMLElement>()
useDocumentEvent('scroll', ()=>{
  //距离窗口的偏移
  const offset = getComputedStyle(anchorRef.value)
    .getPropertyValue('--navbar-height').replace('px', '')
  //元素距离窗口顶部的距离
  const top = anchorRef.value.getBoundingClientRect().top
  const scroll = document.documentElement.scrollTop
  if (scroll <= scrollTop) {
    anchorRef.value.classList.remove('active')
    scrollTop = -1
  }
  else if (top <= Number.parseFloat(offset)) {
    if (!anchorRef.value.classList.contains('active')) {
      scrollTop = scroll
    }
    anchorRef.value.classList.add('active')
  }
})
</script>

<style scoped lang="scss">
.angle {
  clip-path: polygon(50% 0%, 100% 50%, 50% 100%, 50% 48%);
}
.history-item {
  display: flex;
  height: 100px;
  align-items: center;
  position: relative;
  .timetext {
    --angle-w: 15px;
    cursor: pointer;
    position: absolute;
    padding: 8px 10px;
    transform: translateX(calc(var(--left) - 100% - var(--angle-w)));
    background-color: var(--pink0);
    color: #fff;
    border-radius: 6px;
    align-self: flex-start;
    user-select: none;
    font-size: 14px;
    &::after {
      content: '';
      display: block;
      background-color: var(--pink0);
      width: var(--angle-w);
      height: 10px;
      position: absolute;
      left: 100%;
      top: 50%;
      transform: translateX(-50%) translateY(-50%);
      @extend .angle;
    }
    &:empty {
      display: none;
    }
    &.active {
      position: fixed; //包含块为视口
      top: var(--navbar-height);
      z-index: 1; //防止点击失效
    }
  }
  .time {
    padding-left: calc(var(--left) + 20px);
    width: 110px;
    flex-shrink: 0;
    position: relative;
    color: var(--grey2);
    &::before {
      content: '';
      display: block;
      position: absolute;
      width: 10px;
      height: 10px;
      left: calc(var(--left) - 4px);
      background-color: rgba(0,0,0,.2);
      @extend .angle;
    }
  }
  .video-details {
    flex: 1 1 0;
    display: flex;
    align-items: center;
    margin-left: 20px;
    padding-top: 6px;
    padding-bottom: 10px;
    height: 100%;
    box-sizing: border-box;
    position: relative;
    &::after {
      content: '';
      height: 0.5px;
      background-color: rgba(0,0,0,.1);
      position: absolute;
      inset: 0;
      top: auto;
    }
    .left {
      flex: 1 1 0;
      height: 100%;
      display: flex;
      flex-direction: column;
      width: 400px;
      .video-title {
        padding-bottom: 4px;
        font-weight: 550;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      .video-intro {
        color: var(--grey2);
        font-size: 12px;
      }
      .watch-info {
        margin-top: auto;
        font-size: 13px;
        color: var(--grey1);
        display: flex;
        align-items: center;
      }
    }
    .right {
      width: 200px;
      flex-shrink: 0;
      text-align: right;
      .del-icon {
        cursor: pointer;
        &:hover {
          color: rgb(96, 173, 173);
        }
      }
    }
  }
}
</style>