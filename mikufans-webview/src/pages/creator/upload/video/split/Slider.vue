<template>
  <div class="slider" ref="sliderRef" @mousedown="mousedown"></div>
</template>

<script setup lang="ts">
import { useDocumentEvent } from '@/utils/event'
import { clamp } from 'lodash'
import { ref, computed, watch } from 'vue'
const props = defineProps({
  modelValue:Number,
  min: {
    type: Number,
    default: 0,
  },
  max: {
    type: Number,
    default: 1,
  },
  //高度上下延伸多少
  extend: {
    type: String,
    default: '0px',
  }
})
//change每次手动改变都会触发
//changed松手时触发
const emits = defineEmits(['change', 'changed', 'update:modelValue'])
//拖动位置
const value = computed(() => {
  let rate = 1
  let parent = sliderRef.value?.offsetParent as HTMLElement
  if (parent) {
    //子组件无法响应offsetParent的宽度变化
    //必须等待parent加载完毕后再挂载子组件
    rate = parent.scrollWidth / parent.offsetWidth
  }
  return left.value * 100 * rate + '%'
})
const sliderRef = ref<HTMLDivElement>()
const down = ref(false)
const left = ref(0)
var clientX = 0 //按下时的clientX
watch(()=>props.modelValue, val=>{
  if (val !== left.value) {
    setLeft(val)
  }
}, { immediate: true, })
watch(()=>props.min, val=>{
  if(left.value < val) {
    setLeft(val)
    emits('change', left.value)
  }
}, { immediate: true })
watch(()=>props.max, val=>{
  if(left.value > val) {
    setLeft(val)
    emits('change', left.value)
  }
}, { immediate: true })
function setLeft(val: number) {
  left.value = clamp(val, props.min, props.max)
  emits('update:modelValue', left.value)
}
function mousemove(e: MouseEvent | TouchEvent) {
  e.preventDefault()
  if (!down.value) return
  let offsetX = 0
  if (e instanceof MouseEvent) {
    offsetX = e.clientX - clientX
    clientX = e.clientX
  } else {
    offsetX = e.touches[0].clientX
    clientX = e.touches[0].clientX
  }
  let offset = offsetX / maxWidth()
  setLeft(left.value + offset)
  emits('change', left.value)
}
function mousedown(e: MouseEvent | TouchEvent) {
  e.preventDefault()
  if (e instanceof MouseEvent) {
    clientX = e.clientX
  } else {
    clientX = e.touches[0].clientX
  }
  down.value = true
  mousemove(e)
}
function mouseup() {
  if (down.value) {
    emits('changed', left.value)
  }
  down.value = false
}
//父容器最大宽度=>可滚动距离
function maxWidth() {
  //offsetParent最近的包含块,带有定位属性
  let parent = sliderRef.value?.offsetParent
  if (parent instanceof HTMLElement) {
    return Math.max(parent.offsetWidth, parent.scrollWidth)
  } else {
    return 0
  }
}
useDocumentEvent('mouseup', mouseup)
useDocumentEvent('mousemove', mousemove)
defineExpose({
  element() {
    return sliderRef.value
  }
})
</script>

<style scoped lang="scss">
.slider {
  width: 5px;
  cursor: pointer;
  z-index: 1;
  position: absolute;
  top: calc(v-bind(extend) * -1);
  bottom: calc(v-bind(extend) * -1);
  // left是相对包含块的offsetWidth并非scrollWidth
  left: v-bind(value);
  transform: translateX(-50%);
  background-color: #d1d1d1;
  user-select: none;
  -webkit-user-drag: none;
  &:hover {
    background-color: #ece3e3;
  }
}
</style>