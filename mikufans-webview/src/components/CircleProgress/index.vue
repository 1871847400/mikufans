<template>
  <div ref="el" class="circle-progress">
    <slot></slot>
    <!-- 防止模糊,考虑乘上dpr -->
    <canvas ref="canvas"></canvas>
  </div>
</template>

<script setup lang="ts">
import { clamp } from 'lodash';
const props = defineProps({
  thickness: number().def(2), //圈的厚度
  distance: string().def('6px'), //圈往外偏移
  color: string().def('#fc8bab'),
})
//进度比例 0-1
const value = defineModel('modelValue', { default: 0 })
const el = ref<HTMLElement>()
const canvas = ref<HTMLCanvasElement>()
onMounted(()=>{
  watchImmediate(value, ()=>{
    const { clientWidth, clientHeight } = canvas.value
    if (clientWidth == 0 || clientHeight == 0) {
      return
    }
    canvas.value.width = clientWidth
    canvas.value.height = clientHeight
    const ctx = canvas.value.getContext('2d')
    ctx.beginPath(); // 开始一个新的路径
    const radius = clientWidth / 2 - 1; //半径需要比实际小点
    const unit = Math.PI / 2 //四分之一圆的弧度
    //弧度0的起点在右中，而不是中上
    const rate = clamp(value.value, 0, 1)
    ctx.arc(clientWidth / 2, clientHeight / 2, radius, -unit, unit * 4 * rate - unit); // 绘制圆形
    ctx.strokeStyle = props.color //线颜色
    ctx.lineWidth = props.thickness //线宽
    ctx.stroke(); //描边路径
  })
})
</script>

<style scoped lang="scss">
.circle-progress {
  display: inline-block;
  position: relative;
  padding: v-bind(distance);
  canvas {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
  }
}
</style>