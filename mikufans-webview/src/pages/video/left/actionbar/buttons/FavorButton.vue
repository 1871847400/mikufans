<template>
  <div
    class="action-item" 
    title="长按一键三连"
    :class="{ active: video.likeStatus.likeVal==1, longpress }"
    @mousedown="mousedown"
    @mouseleave="mouseleave"
  >
    <circle-progress v-model="triple.progress">
      <div ref="iconRef">
        <i class="iconfont icon-dianzan_kuai text-[28px]"></i>
      </div>
    </circle-progress>
    <action-number :number="video.likeStatus.likes"/>
  </div>
</template>

<script setup lang="ts">
import { useWindowEvent } from '@/utils/event';
import ActionNumber from '../ActionNumber.vue';
import { splash } from '@/utils/animation';
import { clamp } from 'lodash';
const { video } = toRefs(useVideoStore())
const { like, triple } = useVideoStore()
const { q } = useMagicKeys()
watch(q, (pressed)=>{
  if (pressed && document.activeElement.tagName === 'BODY') {
    like()
  }
})
const iconRef = ref<HTMLDivElement>(null)
let timer = null
const longpress = ref(false)
function mousedown() {
  timer = setTimeout(() => {
    longpress.value = true
  }, 350);
}
function mouseleave() {
  clean()
}
function clean() {
  clearTimeout(timer)
  timer = null
  longpress.value = false
}
useWindowEvent('mouseup', ()=>{
  //如果使用@click会在长按后依然触发
  if (timer && !longpress.value) {
    like()
  }
  clean()
})
function renderAnim() {
  let progress = triple.progress
  if (longpress.value) {
    progress += 0.015
    if (progress >= 1) {
      triple.trigger.trigger()
      clean()
      progress = 0
    }
  } else {
    progress -= 0.015
  }
  triple.progress = clamp(progress, 0, 1)
  requestAnimationFrame(renderAnim)
}
renderAnim()
function handle() {
  splash({
    container: iconRef.value,
    offset: 0,
    color: triple.color_dark,
    size: 12,
  })
  splash({
    container: iconRef.value,
    offset: 22,
    color: triple.color_light,
    size: 8,
    delay: 0.1,
  })
  if (video.value.likeStatus.likeVal !== 1) {
    like()
  }
}
onBeforeUnmount(triple.trigger.on(handle).off)
</script>

<style scoped lang="scss">
/* .quake:active */
.longpress {
  animation: 
    quakeX 50ms infinite linear alternate,
    quakeY 75ms infinite linear alternate;
  /* animation-delay: .5s; */
}
@keyframes quakeX {
  to {
    transform: translateX(2px);
  }
}
@keyframes quakeY {
  to {
    transform: translateY(2px);
  }
}
</style>