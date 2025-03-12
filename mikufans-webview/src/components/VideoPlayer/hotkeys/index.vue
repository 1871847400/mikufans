<template>
  <div class="hot-keys">
    <span class="volume abs-center" v-if="volume!=undefined">
      <i class="iconfont icon-24gl-volumeHigh"></i>
      <span>{{ volume*100|0 }}%</span>
    </span>
    <span class="muted abs-center" v-if="muted">
      {{ videoElement.muted ? '静音' : '取消静音' }}
    </span>
    <span class="muted abs-center" v-if="danmu">
      {{ danmuSetting.enable ? '开启弹幕' : '关闭弹幕' }}
    </span>
    <span class="playrate" v-if="longpress">
      <i class="iconfont icon-24gf-play"></i>
      <i class="iconfont icon-24gf-play"></i>
      <i class="iconfont icon-24gf-play"></i>
      <span>{{ videoElement.playbackRate }}倍速播放中</span>
    </span>
  </div>
</template>

<script setup lang="ts">
import { clamp, throttle } from 'lodash'
import { useLongPress } from '../functions/longpress'
import { danmuSetting } from '../danmubar/config'
const activeElement = useActiveElement()
const { videoElement, playerElement } = toRefs(useVideoStore())
const { toggle } = useFullscreen(playerElement)
const volume = customRef((track, trigger)=>{
  let visible = false
  let timer = null
  return {
    get() {
      track()
      return visible ? videoElement.value?.volume : undefined
    },
    set(val) {
      videoElement.value.volume = clamp(val, 0, 1)
      visible = true
      trigger()
      clearTimeout(timer)
      timer = setTimeout(() => {
        visible = false
        trigger()
      }, 2000);
    }
  }
})
const message = refAutoReset('', 2000)
const muted = customRef((track, trigger)=>{
  let visible = false
  let timer = null
  return {
    get() {
      track()
      return visible
    },
    set(val) {
      videoElement.value.muted = val
      visible = true
      trigger()
      clearTimeout(timer)
      timer = setTimeout(() => {
        visible = false
        trigger()
      }, 2000);
    }
  }
})
const danmu = customRef((track, trigger)=>{
  let visible = false
  let timer = null
  return {
    get() {
      track()
      return visible
    },
    set(val) {
      danmuSetting.value.enable = val
      visible = true
      trigger()
      clearTimeout(timer)
      timer = setTimeout(() => {
        visible = false
        trigger()
      }, 2000);
    }
  }
})
//是否激活按键功能
const active = computed(()=>videoElement.value && activeElement.value?.tagName==='BODY')
//按键持续触发
useEventListener('keydown', throttle((e)=>{
  if (active.value) {
    switch (e.key.toLowerCase()) {
      case 'arrowleft':
        videoElement.value.currentTime -= 5;
        break
      case 'arrowup':
        volume.value = videoElement.value.volume + 0.1
        break
      case 'arrowdown':
        volume.value = videoElement.value.volume - 0.1
        break
      case 'f':
        toggle()
        break
      case 'm':
        muted.value = !videoElement.value.muted
        break
      case 'd':
        danmu.value = !danmuSetting.value.enable
        break
      case ' ':
        if (videoElement.value.paused) {
          videoElement.value.play()
        } else {
          videoElement.value.pause()
        }
        break
      default:
        return
    }
    e.preventDefault()
  }
}, 100))
const longpress = useLongPress(active, videoElement)
</script>

<style scoped lang="scss">
.hot-keys {
  position: absolute;
  inset: 0;
  user-select: none;
  pointer-events: none;
  .volume {
    background: #ffffff93;
    padding: 10px;
    color: #333;
    border-radius: 4px;
    font-size: 20px;
    .iconfont {
      margin-right: 4px;
      font-weight: 550;
    }
  }
  .muted {
    background: #000;
    color: #fff;
    padding: 10px;
    font-size: 16px;
    border-radius: 4px;
  }
  .playrate {
    background: #000;
    color: #fff;
    padding: 10px;
    font-size: 14px;
    border-radius: 4px;
    position: absolute;
    top: 10px;
    left: 50%;
    transform: translateX(-50%);
    .iconfont {
      animation: flicker linear .3s alternate infinite both;
    }
    .iconfont:nth-of-type(2) {
      animation-delay: .1s;
    }
    .iconfont:nth-of-type(3) {
      animation-delay: .2s;
      margin-right: 4px;
    }
    @keyframes flicker {
      0% {
        opacity: 0.2;
      }
      100% {
        opacity: 1;
      }
    }
  }
}
</style>