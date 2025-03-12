<template>
  <danmu-box v-if="videoElement" :danmus="danmuList" :options="danmuSetting" :video="videoElement" />
</template>

<script setup lang="ts">
import { uniqBy } from 'lodash';
import { danmuSetting } from '../danmubar/config';
import videoDanmuApi from '@/apis/video/danmu';
const { videoElement, videoPart, danmus } = toRefs(useVideoStore())
const userStore = useUserStore()
const danmuList = computed(()=>{
  return uniqBy(danmus.value, 'id').map(convert)
})
function convert(danmu: VideoDanmu) {
  return {
    id: danmu.id,
    color: danmu.fontColor,
    text: danmu.content,
    time: danmu.sendTime,
    type: danmu.danmuType,
    self: userStore.isSelf(danmu.userId),
  } as Danmu
}
//载入最新的弹幕
async function loadNews() {
  if (videoPart.value) {
    const minId = danmus.value[danmus.value.length - 1]?.id
    const result = await videoDanmuApi.listByPartId(videoPart.value.id, { minId })
    //追加新的弹幕
    danmus.value.push(...result)
  }
}
watchImmediate(videoPart, ()=>{
  danmus.value = []
  loadNews()
})
//轮询获取最新弹幕
useIntervalFn(loadNews, 10_000)
</script>

<style scoped lang="scss">
</style>