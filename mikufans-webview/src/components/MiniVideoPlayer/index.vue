<template>
  <div class="mini-video-player">
    <video 
      ref="video"
      :poster="poster"
      @timeupdate="timeupdate" 
      @durationchange="durationchange"
      :controls="false" 
      muted 
      autoplay 
      loop>
    </video>
    <div class="mask"></div>
    <div class="absolute bottom-1 right-1 text-white text-xs">
      <span>{{ displayDuration(currentTime*1000) }}/{{ displayDuration(duration*1000) }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import videoResApi from '@/apis/video/resource';
import Hls from 'hls.js';
import { createHls } from '../VideoPlayer/functions/hls';
import { displayDuration } from '@/utils/common';
import { imageUrl } from '@/apis/image';
import videoPartApi from '@/apis/video/part';
const props = defineProps<{ data: Video }>()
const { state } = useAsyncState(videoPartApi.getFirstCanplay(props.data.id), null)
const emits = defineEmits<{ timeupdate: [number] }>()
const video = ref<HTMLVideoElement>()
const poster = computed(()=> imageUrl + props.data.bannerId)
let hls : Hls = null
const currentTime = ref(0)
const duration = ref(0)
function timeupdate() {
  currentTime.value = video.value?.currentTime ?? 0
  emits('timeupdate', currentTime.value)
}
function durationchange() {
  duration.value = video.value?.duration ?? 0
}
watchOnce(state, ()=>{
  const masterUrl = videoResApi.getMasterUrl(state.value.id)
  hls = createHls()
  hls.loadSource(masterUrl)
  hls.attachMedia(video.value)
})
onUnmounted(()=>{
  hls?.destroy()
  video.value?.remove()
  video.value = null
})
//keep-live激活时(也包括第一次)
onActivated(()=>{
  //如果video还在dom树内接着播放
  if (video.value?.isConnected) {
    video.value.play()
  }
})
//组件暂时卸载时暂停播放
onDeactivated(()=>{
  if (video.value?.paused === false) {
    video.value?.pause()
  }
})
</script>

<style lang="scss" scoped>
.mini-video-player {
  width: 100%;
  height: auto;
  aspect-ratio: 16 / 9;
  background-color: #000;
  overflow: hidden;
  position: relative;
  video {
    width: 100%;
    height: 100%;
    pointer-events: none;
  }
  .mask {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    top: 80%;
    background: var(--img-shadow);
  }
}
</style>