<template>
  <div class="text-sm grey1">{{ watching }} 人正在看，已装填 {{ displayNumber(danmus.length) }} 条弹幕</div>
</template>

<script setup lang="ts">
import videoOnlineApi from "@/apis/video/online"
import { displayNumber } from "@/utils/common"
import videoHistoryApi from '@/apis/video/history';
const { video, videoPart, danmus, videoElement } = toRefs(useVideoStore())
const userStore = useUserStore()
const watching = ref(1)
//记录当前播放位置
function recordPosition() {
  if (videoPart.value && userStore.isLogin && videoElement.value) {
    return videoHistoryApi.record({
      videoId: videoPart.value.videoId,
      partId: videoPart.value.id,
      watchPos: Math.trunc(videoElement.value.currentTime * 1000),
    })
  }
}
//刷新当前观看人数
function refreshWatchCount() {
  if (video.value) {
    videoOnlineApi.getOnline(video.value.id).then(val=>{
      watching.value = Math.max(1, val)
    })
  }
}
//定时记录播放位置
useIntervalFn(recordPosition, 15_000, { immediate: false })
//关闭视频时记录播放位置
onBeforeUnmount(recordPosition)
//定时刷新当前观看人数
useIntervalFn(refreshWatchCount, 30_000, { immediate: true })
//当视频第一次能播放时记录位置
useEventListener(videoElement, 'canplay', recordPosition, { once: true })
</script>

<style scoped lang="scss">
</style>