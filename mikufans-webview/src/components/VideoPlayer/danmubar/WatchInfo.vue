<template>
  <div class="text-sm grey1">{{ watching }} 人正在看，已装填 {{ displayNumber(danmus.length) }} 条弹幕</div>
</template>

<script setup lang="ts">
import videoOnlineApi from "@/apis/video/online"
import { displayNumber } from "@/utils/common"
import videoHistoryApi from '@/apis/video/history';
import { getHeartbeat } from "@/apis/middle/heartbeat";
const { video, videoPart, danmus, videoElement } = toRefs(useVideoStore())
const userStore = useUserStore()
const watching = ref(1)
//记录当前播放位置
function recordPosition() {
  if (videoPart.value && userStore.isLogin && videoElement.value) {
    videoHistoryApi.record({
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
//定时记录播放位置和刷新当前观看人数
useIntervalFn(()=>{ recordPosition(); refreshWatchCount() }, 10_000, { immediate: true })
//关闭视频时记录播放位置
onBeforeUnmount(recordPosition)
//当视频第一次能播放时记录位置
useEventListener(videoElement, 'canplay', recordPosition, { once: true })

// async function heartbeat() {
//   if (videoPart.value && userStore.isLogin && videoElement.value) {
//     const data = await getHeartbeat({
//       videoId: videoPart.value.videoId,
//       partId: videoPart.value.id,
//       watchPos: Math.trunc(videoElement.value.currentTime * 1000),
//     })
//     console.log(data);
//   }
// }
// useIntervalFn(heartbeat, 5_000, { immediate: true })
</script>

<style scoped lang="scss">
</style>