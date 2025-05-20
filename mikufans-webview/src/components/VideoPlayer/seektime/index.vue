<template>
  <div class="seek-time" v-if="msg">{{ msg }}</div>
</template>

<script setup lang="ts">
import { displayDuration } from '@/utils/common'
import { toNumber } from 'lodash'
const { video, videoPart, videoElement } = toRefs(useVideoStore())
const msg = customRef((track, trigger)=>{
  let value = ''
  let timer = null
  return {
    get() {
      track()
      return value
    },
    set(val) {
      clearTimeout(timer)
      value = val
      trigger()
      timer = setTimeout(() => {
        value = ''
        trigger()
      }, 3000);
    }
  }
})
const HASH_PREFIX = '#time'
const hash = useRouteHash('')
// const seekTime = useRouteQuery('t', 0, { transform: toNumber })
useEventListener(videoElement, 'loadedmetadata', onLoad)
//加载元数据后,注意如果用@canplay会多次触发
function onLoad() {
  let seek = 0
  const history = video.value.history
  //优先使用最晚的播放记录
  if (history?.partId === videoPart.value.id) {
    seek = history.watchPos / 1000
    logger.debug('播放记录',seek)
  }
  //如果使用了参数定位播放起点
  if (hash.value?.startsWith(HASH_PREFIX)) {
    const seekTime = toNumber(hash.value.replace(HASH_PREFIX, ''))
    if (seekTime > seek) {
      seek = seekTime
      hash.value = ''
      logger.debug('定位参数',seek)
    }
  }
  if (seek && videoElement.value.duration - seek > 1) {
    videoElement.value.currentTime = seek
    msg.value = '已为您定位至' + displayDuration(seek*1000, true)
  }
}
</script>

<style scoped lang="scss">
.seek-time {
  position: absolute;
  pointer-events: none;
  user-select: none;
  left: 10px;
  bottom: 80px;
  background: #000000b5;
  border-radius: 4px;
  padding: 10px;
  color: #fff;
}
</style>