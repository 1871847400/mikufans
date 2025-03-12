<template>
  <div ref="el"
    class="media-progress"
    :class="{ seeking }"
    :style="{
      '--current': value,
      '--buffered': clamp(buffer/duration, 0, 1)*100 + '%',
      '--indicator': clamp(elementX/elementWidth, 0, 1)*100 + '%',
    }"
  >
    <div class="duration">
      <div class="buffered"></div>
      <div class="current"></div>
    </div>
    <div class="circle"></div>
    <div class="indicator">
      <i class="iconfont icon-xiangxia"></i>
      <i class="iconfont icon-xiangshang"></i>
    </div>
    <div
      ref="thumnailRef"
      class="thumbnail"
      :style="{ 
        width: `${thumnail.w}px`,
        height: `${thumnail.h}px`,
        left: `${thumbLeft}px`,
        backgroundImage: `url('${imgUrl}')`,
        backgroundPositionX: `${-thumnail.x}px`,
        backgroundPositionY: `${-thumnail.y}px`,
      }"
    >
      <div class="time">{{ displayDuration(thumnail.time*1000) }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { displayDuration } from '@/utils/common';
import { clamp } from 'lodash';
import { useThumbnail } from './webvtt';
const props = defineProps<{
  video: HTMLVideoElement
  vttUrl: string,
  imgUrl: string
}>()
const { video } = toRefs(props)
const current = customRef((track, trigger)=>{
  useEventListener(video, 'timeupdate', ()=>{
    if (!seeking.value) {
      trigger()
    }
  })
  useEventListener(video, 'seeked', ()=>{
    trigger()
  })
  return {
    get() {
      track()
      return video.value?.currentTime || 0
    },
    set(value) {
      if (video.value) {
        video.value.currentTime = value
      }
    },
  }
})
const duration = customRef((track, trigger)=>{
  useEventListener(video, 'durationchange', ()=>trigger())
  return {
    get() {
      track()
      return video.value?.duration || 0
    },
    set(value) {}
  }
})
const buffer = customRef<number>((track, trigger)=>{
  useEventListener(video, 'progress', (e)=>{
    trigger()
  })
  return {
    get: ()=>{
      track()
      const buffered = video.value?.buffered
      if (buffered?.length) {
        return buffered.end(buffered.length - 1)
      }
      return 0
    },
    set() {}
  }
})
const seeking = ref(false) //是否正在调整时间
const thumbnails = useThumbnail(toRef(props, 'vttUrl'))
// buffered.value = videoElement.value.currentTime + 3
const el = useTemplateRef('el')
const { elementX, elementWidth } = useMouseInElement(el, {})
const value = computed(()=>{
  if (seeking.value) {
    return clamp(elementX.value / elementWidth.value, 0, 1) * 100 + '%'
  }
  return current.value / duration.value * 100 + '%'
})
const thumnail = computed(()=>{
  const position = clamp(elementX.value / elementWidth.value, 0, 1) * duration.value
  for (const line of thumbnails.value) {
    if (line.start <= position && line.end >= position) {
      return { ...line, time: position }
    }
  }
  return { x:0, y:0, w:0, h:0, time: position }
})
const thumnailRef = useTemplateRef('thumnailRef')
const thumbLeft = computed(()=>{
  const clientWidth = thumnailRef.value?.clientWidth || 0
  return clamp(elementX.value, clientWidth/2, elementWidth.value - clientWidth/2)
})
useEventListener(el, 'mousedown', ()=>{
  seeking.value = true
  const callback = ()=>{
    current.value = elementX.value / elementWidth.value * duration.value
    window.removeEventListener('mouseup', callback)
    seeking.value = false
  }
  window.addEventListener('mouseup', callback)
})
</script>

<style scoped lang="scss">
.media-progress {
  width: 100%;
  height: 8px;
  /* background-color: red; */
  display: flex;
  align-items: center;
  position: relative;
  cursor: pointer;
  user-select: none;
  //总时长
  .duration {
    border-radius: 4px;
    flex: 1;
    height: 6px;
    background-color: #adaeaa;
    position: relative;
    //缓存进度
    .buffered {
      width: var(--buffered);
      height: 100%;
      background-color: #ccc;
      position: absolute;
      left: 0;
      border-radius: 4px;
    }
    //播放进度
    .current {
      width: var(--current);
      height: 100%;
      background-color: #00a1d6;
      position: absolute;
      left: 0;
      border-radius: 4px;
    }
  }
  &:hover, &.seeking {
    .duration {
      height: 100%;
    }
    .indicator, .circle, .thumbnail {
      visibility: visible;
    }
  }
  .circle {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background-color: #eee;
    border: 1px solid #ccc;
    position: absolute;
    left: var(--current);
    top: 50%;
    transform: translateX(-50%) translateY(-50%);
    visibility: hidden;
  }
  .indicator {
    color: #00aeec;
    position: absolute;
    transform: translateX(-50%) translateY(-50%);
    font-size: 10px;
    top: 50%;
    left: var(--indicator);
    pointer-events: none;
    display: flex;
    flex-direction: column;
    gap: 10px;
    visibility: hidden;
  }
  .thumbnail {
    visibility: hidden;
    position: absolute;
    top: calc(-100% - 20px); //20px可以改
    transform: translateX(-50%) translateY(-100%);
    display: flex;
    justify-content: center;
    background-color: #000;
    background-repeat: no-repeat;
    pointer-events: none;
    .time {
      background: #000;
      padding: 2px 4px;
      color: #fff;
      width: fit-content;
      position: absolute;
      bottom: 4px;
      font-size: 10px;
      border-radius: 4px;
    }
  }
}
</style>