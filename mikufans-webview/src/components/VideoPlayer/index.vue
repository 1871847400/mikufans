<template>
  <div ref="playerElement" class="video-player" :class="{ fullscreen: isFullscreen }">
    <div class="video-container" :class="{ idle }" @contextmenu="showContextMenu" @click="playOrPause" @dblclick="toggle" @mousemove="reset">
      <div v-if="errorMessage">
        <span class="text-[#fff] abs-center text-lg">{{ errorMessage }}</span>
      </div>
      <template v-else>
        <video ref="videoElement" autoplay :controls="false" @contextmenu.prevent></video>
        <video-loading />
        <danmu-box />
        <hot-keys />
        <video-seek />
        <div class="video-player-controls">
          <div class="mask"></div>
          <div class="controls" @click.stop>
            <video-progress />
            <action-bar />
          </div>
        </div>
      </template>
    </div>
    <div class="flex items-center p-2">
      <watch-info />
      <danmu-bar class="ml-auto"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import VideoProgress from './progress/index.vue'
import ActionBar from './actionbar/index.vue'
import DanmuBar from './danmubar/index.vue'
import DanmuBox from './danmubox/index.vue';
import videoResApi from '@/apis/video/resource';
import VideoLoading from './loading/index.vue'
import { showContextMenu } from './functions/contextmenu';
import { createHls } from './functions/hls';
import VideoSeek from './seektime/index.vue'
import HotKeys from './hotkeys/index.vue'
import WatchInfo from './danmubar/WatchInfo.vue';
import { useDefaultSettings } from './functions/settings';
import { Events } from 'hls.js';
const { hls, video, videoPart, videoElement, playerElement } = toRefs(useVideoStore())
const { isFullscreen, toggle } = useFullscreen(playerElement)
onMounted(()=>{
  watchImmediate(videoPart, async part=>{
    errorMessage.value = ''
    if (part) {
      const url = videoResApi.getMasterUrl(part.id)
      hls.value?.detachMedia()
      hls.value?.destroy()
      hls.value = createHls()
      hls.value.loadSource(url)
      hls.value.attachMedia(videoElement.value)
      hls.value.on(Events.ERROR, (ev, data)=>{
        if (data.details === 'manifestLoadError') {
          // console.log('error',data,ev);
          const res = data.networkDetails['responseText'] as string
          const errMsg = JSON.parse(res || '{}')?.data?.message || '该视频无法播放'
          errorMessage.value = errMsg
        }
      })
    }
  })
})
onUnmounted(()=>{
  hls.value?.detachMedia()
  hls.value?.destroy()
  hls.value = null
})

const errorMessage = customRef((track, trigger)=>{
  let msg = ''
  return {
    get() {
      track()
      return !videoPart.value ? '当前视频无法播放' : msg
    },
    set(val) {
      msg = val
      trigger()
    }
  }
})

useDefaultSettings(videoElement)

function playOrPause() {
  if (videoElement.value) {
    if (videoElement.value.paused) {
      videoElement.value.play()
    } else {
      videoElement.value.pause()
    }
  }
}

const { idle, reset } = useIdle(5000, { events: [], })
//全屏下输入弹幕,不隐藏控件
useIntervalFn(()=>{
  const el = document.activeElement
  if (el instanceof HTMLElement && isFullscreen.value) {
    if (el.contentEditable == 'true' || el instanceof HTMLInputElement) {
      reset()
    }
  }
}, 4000)
</script>

<style lang="scss" scoped>
.video-player {
  box-shadow: 0 0 8px #e3e3e3;
  &.fullscreen {
    video::cue{
      font-size: 40px!important;
    }
  }
  .video-container {
    position: relative;
    width: 100%;
    height: auto;
    aspect-ratio: 16 / 9;
    background-color: #000;
    overflow: hidden;
    video {
      width: 100%;
      height: 100%;
    }
    /* 字幕样式 */
    video::cue{
      background-color: transparent;
      color: #fff;
      font-size: 20px;
      text-shadow: #000 1px 1px 4px;
    }
    .video-player-controls {
      transition: opacity .3s;
      .controls {
        position: absolute;
        z-index: 1;
        bottom: 0;
        left: 0;
        right: 0;
        padding: 0 8px;
      }
      .mask {
        position: absolute;
        z-index: 1;
        left: 0;
        right: 0;
        bottom: 0;
        height: 80px;
        pointer-events: none;
        background: linear-gradient(0, #0000008e, #00000010);
      }
    }
    //静止状态隐藏鼠标和停用事件
    &.idle {
      cursor: none;
      .video-player-controls {
        opacity: 0;
        pointer-events: none;
      }
    }
  }
}
</style>