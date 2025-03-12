<template>
  <section>
    <i class="iconfont icon-next"></i>
    <i v-if="paused" class="iconfont icon-24gf-play" @click="videoElement.play()"></i>
    <i v-else class="iconfont icon-24gf-pause2" @click="videoElement.pause()"></i>
    <i class="iconfont icon-next"></i>
    <button v-if="!inputing" class="time-button" @click="onClick">
      <span>{{ displayDuration(current * 1000) }}</span>
      <span>/</span>
      <span>{{ displayDuration(duration * 1000) }}</span>
    </button>
    <form v-else class="time-input" @submit.prevent="inputRef.blur()">
      <input 
        ref="inputRef" 
        v-model="value" 
        type="text" 
        autofocus 
        @blur="onBlur" 
        @change="changed=true"
      >
    </form>
  </section>
</template>

<script setup lang="ts">
import { displayDuration } from '@/utils/common';
const { videoElement } = toRefs(useVideoStore())
const current = ref(videoElement.value?.currentTime || 0)
const duration = ref(videoElement.value?.duration || 0)
const paused = ref(videoElement.value?.paused ?? true)
useEventListener(videoElement, 'timeupdate', ()=>{
  current.value = videoElement.value.currentTime
})
useEventListener(videoElement, 'durationchange', ()=>{
  duration.value = videoElement.value.duration
})
useEventListener(videoElement, ['playing', 'play'], ()=>{
  paused.value = false
})
useEventListener(videoElement, 'pause', ()=>{
  paused.value = true
})
const inputing = ref(false)
const value = ref('')
const inputRef = useTemplateRef('inputRef')
const changed = ref(false)
function onClick() {
  value.value = displayDuration(current.value * 1000)
  changed.value = false
  inputing.value = true
  nextTick(()=>{
    inputRef.value?.focus()
  })
}
function onBlur() {
  inputing.value = false
  if (changed.value) {
    const arr = value.value.split(':').map(Number).toReversed()
    let result = 0
    if (Number.isInteger(arr[0])) {
      result = arr[0]
    }
    if (Number.isInteger(arr[1])) {
      result += arr[1] * 60
    }
    if (Number.isInteger(arr[2])) {
      result += arr[2] * 60 * 60
    }
    videoElement.value.currentTime = result
  }
}
</script>

<style scoped lang="scss">
section {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  & > * {
    color: #f3f5f7f1;
    transition: color .3s;
    &:hover {
      color: #fff;
    }
  }
  i {
    cursor: pointer;
    padding: 0 12px;
  }
  i:nth-of-type(1) {
    font-size: 20px;
    rotate: -180deg;
  }
  i:nth-of-type(2) {
    font-size: 24px;
  }
  i:nth-of-type(3) {
    font-size: 20px;
  }
  .time-button {
    display: flex;
    gap: 4px;
    font-size: 14px;
    background: none;
    cursor: text;
    height: 30px;
    line-height: 30px;
    width: 85px;
  }
  .time-input {
    width: 85px;
    height: 30px;
    line-height: 30px;
    background-color: #333;
    display: flex;
    justify-content: center;
    input {
      max-width: 60px;
      text-align: center;
      background: none;
      color: #fff;
    }
  }
}
</style>