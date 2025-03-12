<template>
  <div 
    ref="container" 
    class="danmu-box"
    data-only
    :class="{
      pause, 
      'hidden-roll': options.ban.roll,
      'hidden-fixed': options.ban.fixed,
      'hidden-color': options.ban.color,
    }" 
    :style="{
      '--opacity': options.opacity+'',
      '--visible': options.enable?'visible':'hidden',
    }"
    v-resize="onResize"
  >
  </div>
</template>

<script setup lang="ts">
import { DanmuBox } from './danmu';
import { render } from 'vue';
import Popover from './Popover.vue';
const props = defineProps<{
  options: DanmuBoxOptions,
  video: HTMLVideoElement
  danmus: Danmu[]
}>()
const { options, video, danmus } = toRefs(props)
const container = useTemplateRef('container')
let danmuBox : DanmuBox = null
//当弹幕容器大小变化时,尤其出现在全屏切换时
function onResize() {
  //清除弹幕
  danmuBox?.clearAll()
  //更新轨道
  danmuBox?.updateTracks()
}
watch(()=>options.value.syncVideo, ()=>{
  danmuBox?.updateSpeed()
})
useEventListener(video, 'ratechange', ()=>{
  danmuBox?.updateSpeed()
})
onMounted(()=>{
  danmuBox = new DanmuBox(container.value, video.value, options.value)
  function sendDanmu(danmu: Danmu) {
    const el = danmuBox.render(danmu)
    if (el) {
      el.addEventListener('mouseenter', e=>{
        const vnode = h(Popover, {
          x: e.offsetX, 
          danmu, 
          onRemove() {
            danmuBox.removeById(danmu.id)
          }
        })
        render(vnode, el)
        const callback = ()=>{
          render(null, el)
          el.removeEventListener('mouseleave', callback)
        }
        el.addEventListener('mouseleave', callback)
      })
    }
  }
  useEventListener(video, 'timeupdate', ()=>{
    const time = video.value.currentTime
    for(const danmu of danmus.value) {
      const start = danmu.time
      const end = danmu.time + options.value.duration/1000
      if (time >= start && time < end) {
        sendDanmu(danmu)
      }
    }
  })
})
const pause = customRef((track, trigger)=>{
  useEventListener(video, ['play', 'playing', 'pause'], ()=>trigger())
  return {
    get() {
      track()
      return video.value?.paused ?? true
    },
    set() {}
  }
})
</script>

<style lang="scss">
.danmu-box[data-only] {
  inset: 0;
  position: absolute;
  overflow: hidden;
  &.pause .danmu-item {
    animation-play-state: paused;
  }
  &.hidden-roll .danmu-item[data-type='roll'],
  &.hidden-fixed .danmu-item[data-type='fixed_top'],
  &.hidden-fixed .danmu-item[data-type='fixed_bottom'],
  &.hidden-color .danmu-item[data-color='true'] {
    visibility: hidden;
    pointer-events: none;
  }
  .danmu-item {
    position: absolute;
    left: 100%;
    top: var(--top);
    white-space: nowrap;
    user-select: none;
    visibility: var(--visible); //不使用display防止无法计算盒子模型
    opacity: var(--opacity);
    animation: danmu-play var(--duration) linear forwards;
    color: var(--color);
    text-shadow: 1px 0 1px #000, 0 1px 1px #000, 0 -1px 1px #000, -1px 0 1px #000;
    z-index: 1;
    font-family: var(--font);
    font-size: var(--fontSize);
    line-height: var(--lineHeight);
    &:hover {
      animation-play-state: paused;
      z-index: 2; //防止被后面的弹幕覆盖
      /* .popover {
        display: block;
      } */
    }
    &[data-type='fixed_top'], &[data-type='fixed_bottom'] {
      animation-name: danmu-play2;
    }
    &[data-self='true'] {
      /* box-shadow: 0 0 1px #eee; */
      border: 2px solid #eee;
      padding: 0 4px;
      border-radius: 4px;
    }
    img.emoji {
      width: 1.3em;
      height: 1.3em;
      cursor: pointer;
      &[data-size="2"] {
        width: 1.6em;
        height: 1.6em;
      }
    }
  }
  /* 滚动弹幕 */
  @keyframes danmu-play {
    0% {
      transform: translateX(0);
    }
    100% {
      transform: translateX(var(--offset));
    }
  }
  /* 固定弹幕 */
  @keyframes danmu-play2 {
    0% {
      transform: translateX(var(--offset));
    }
    100% {
      transform: translateX(var(--offset));
    }
  }
}
</style>