<template>
  <section class="flex items-center gap-2">
    <el-popover v-if="videoPart?.subtitles?.length" trigger="hover" effect="dark" width="auto" placement="top" :teleported="false">
      <ul class="option-list">
        <template v-for="i in videoPart.subtitles">
          <li :class="{active:subtitle?.id===i.id}" @click="subtitle=i">{{ i.region.langName }}</li>
        </template>
        <li @click="subtitle=null">关闭字幕</li>
      </ul>
      <template #reference>
        <span class="option-tip">{{ subtitle ? subtitle.region.langName : '字幕'}}</span>
      </template>
    </el-popover>
    <el-popover trigger="hover" effect="dark" width="auto" placement="top" :teleported="false">
      <ul class="option-list">
        <template v-for="{ label, value } in levels">
          <li :class="{active:level==value}" @click="changeLevel(value)">{{ label }}</li>
        </template>
      </ul>
      <template #reference>
        <span class="option-tip">{{ levels.find(a=>a.value==level)?.label || '画质' }}</span>
      </template>
    </el-popover>
    <el-popover trigger="hover" effect="dark" width="auto" placement="top" :teleported="false">
      <ul class="option-list">
        <template v-for="{ label, value } in speeds">
          <li :class="{active:playrate==value}" @click="playrate=value">{{ label }}</li>
        </template>
      </ul>
      <template #reference>
        <span class="option-tip">{{ playrate==1 ? '倍数': (speeds.find(a=>a.value==playrate)?.label ?? playrate+'X') }}</span>
      </template>
    </el-popover>
    <el-popover trigger="hover" effect="dark" width="auto" placement="top" :teleported="false">
      <div class="volume-change">
        <div class="text-center">{{ volume*100|0 }}%</div>
        <el-slider vertical height="100px" v-model="volume" :min="0" :max="1" :step="0.01" size="small" :show-tooltip="false" />
      </div>
      <template #reference>
        <span class="option-tip text-[24px]" @click="muted=!muted">
          <i class="iconfont">{{ muted ? '&#xe714;' : '&#xe718;' }}</i>
        </span>
      </template>
    </el-popover>
    <span class="option-tip text-[24px]" @click="fullscreen.toggle()">
      <i class="iconfont icon-quanping2"></i>
    </span>
  </section>
</template>

<script setup lang="ts">
import { Events } from 'hls.js'
import { toInteger } from 'lodash'
import { addSubtitleTrack, clearSubtitleTracks } from '../functions/subtitle'
import logger from '@/utils/logger';
const { hls, videoElement, playerElement, videoPart, videoSettings } = toRefs(useVideoStore())
const subtitle = customRef((track, trigger)=>{
  let value : VideoSubtitle = null
  return {
    get() {
      track()
      return value
    },
    set(val) {
      value = val
      trigger()
      if (value) {
        addSubtitleTrack(val, videoElement.value)
      } else {
        clearSubtitleTracks(videoElement.value)
      }
    }
  }
})
const playrate = customRef((track, trigger)=>{
  useEventListener(videoElement, 'ratechange', ()=>{
    trigger()
  })
  return {
    get() {
      track()
      return videoElement.value?.playbackRate ?? 1
    },
    set(value) {
      videoElement.value.playbackRate = value
    },
  }
})
const volume = customRef((track, trigger)=>{
  //volumechange监听 volume和muted 变化
  useEventListener(videoElement, 'volumechange', ()=>{
    trigger()
  })
  return {
    get() {
      track()
      return videoElement.value?.muted ? 0 : videoElement.value?.volume ?? 1
    },
    set(value) {
      if (value > 0 && videoElement.value.muted) {
        muted.value = false
      }
      videoElement.value.volume = value
    }
  }
})
const muted = customRef((track, trigger)=>{
  useEventListener(videoElement, 'volumechange', ()=>{
    trigger()
  })
  return {
    get() {
      track()
      return videoElement.value?.muted || volume.value<=0
    },
    set(value) {
      videoElement.value.muted = value
      trigger()
    }
  }
})
const speeds = [
  { label: '3.0X', value: 3 },
  { label: '2.0X', value: 2 },
  { label: '1.5X', value: 1.5 },
  { label: '1.25X', value: 1.25 },
  { label: '1.0X', value: 1 },
  { label: '0.75X', value: 0.75 },
  { label: '0.5X', value: 0.5 },
]
const fullscreen = useFullscreen(playerElement, { autoExit: true })

const levels = reactive<Option[]>([])
watchImmediate(hls, ()=>{
  levels.length = 0
  hls.value?.on(Events.MANIFEST_LOADED, (ev, data)=>{
  logger.debug('MANIFEST_LOADED: ', data)
    if (data.levels) {
      for (const level of data.levels) {
        const url = level.url
        let label = '标清'
        if (url.includes('/fhd/')) {
          label = '超清'
        } else if (url.includes('/hd/')) {
          label = '高清'
        }
        levels.push({
          label, value: data.levels.indexOf(level),
        })
      }
    }
  })
})
const level = customRef((track, trigger)=>{
  watchImmediate(hls, ()=>{
    //手动切换或自动切换
    hls.value?.on(Events.LEVEL_SWITCHED, (ev, data)=>{
      logger.debug('LEVEL_SWITCHED', data)
      trigger()
    })
    hls.value?.on(Events.LEVEL_LOADED, (ev, data)=>{
      logger.debug('LEVEL_LOADED: ', data)
      trigger()
    })
  })
  return {
    get() {
      track()
      return hls.value?.currentLevel ?? 0
    },
    set(value) {
      hls.value.currentLevel = value
    }
  }
})
//手动切换画质
function changeLevel(value: any) {
  level.value = toInteger(value)
  videoSettings.value.level = toInteger(value)
}
</script>

<style scoped lang="scss">
section {
  color: #f3f3f3;
}
ul.option-list {
  width: 80px;
  user-select: none;
  padding: 4px 0;
  li {
    text-align: center;
    height: 30px;
    line-height: 30px;
    cursor: pointer;
    &:hover {
      color: #fff;
      background-color: #4c4b4a;
    }
    &.active {
      color: var(--blue0);
    }
  }
}
.option-tip {
  user-select: none;
  width: 50px;
  text-align: center;
  cursor: pointer;
  color: #ffffffce;
  transition: color .2s;
  &:hover {
    color: #fff;
  }
}
.volume-change {
  width: 60px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 0;
  .el-slider {
    --el-slider-button-size: 15px;
    /* height: 100px; */
    margin-top: 10px;
  }
}
</style>