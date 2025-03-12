<template>
  <div class="video-loading" v-if="loading">
    <img :src="Loading" alt="" style="width: 1.5em;">
    <span>加载中</span>
  </div>
</template>

<script setup lang="ts">
import Loading from '@/assets/images/loading2.gif'
const { videoElement } = toRefs(useVideoStore())
const loading = ref(true)
let timer = null
useEventListener(videoElement, ['play', 'playing', 'canplay'], ()=>{
  clearTimeout(timer)
  loading.value = false
})
useEventListener(videoElement, ['stalled', 'waiting'], ()=>{
  timer = setTimeout(() => {
    loading.value = true
  }, 200);
})
</script>

<style scoped lang="scss">
.video-loading {
  position: absolute;
  inset: 0;
  background-color: #00000041;
  pointer-events: none;
  user-select: none;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 4px;
}
</style>