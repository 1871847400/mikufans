<template>
  <div class="circle" :class="{ half }" :style="{'--deg':current + 'deg'}">
    <div class="before"></div>
    <div class="mask"></div>
    <div class="after"></div>
    <div class="content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  deg: number().def(0), //转动的目标角度
  color: string().def('#fc8bab'), //圈的颜色
  thickness: string().def('4px'), //圈的厚度
  gap: string().def('4px'), //圈离中心的距离
  anim: bool().def(true),//启用动画效果
})
//转动角度事件
const emits = defineEmits<{ change: [val: number] }>()
//真实转动角度
const current = ref(0)
useIntervalFn(()=>{
  if (!props.anim) {
    if (current.value !== props.deg) {
      current.value = props.deg
      emits('change', current.value)
    }
  } else if (current.value < props.deg) {
    current.value = Math.min(current.value + 3, props.deg)
    emits('change', current.value)
  } else if (current.value > props.deg) {
    current.value = Math.max(current.value - 3, props.deg)
    emits('change', current.value)
  }
}, 10)
//已经转动半圈
const half = computed(()=>current.value>=180)
</script>

<style scoped lang="scss">
.circle {
  --color: v-bind(color);
  --thickness: v-bind(thickness);
  --gap: v-bind(gap);
  position: relative;
  &.half .mask::before {
    background: none;
  }
  &.half .after::before {
    display: block;
  }
  /* 作为转动轴 */
  .before {
    position: absolute;
    width: calc(100% + var(--thickness));
    height: calc(100% + var(--thickness));
    left: 50%;
    top: 50%;
    transform: translateX(-50%) translateY(-50%) rotate(var(--deg));
    /* transition: transform .3s; */
    border-radius: 50%;
    overflow: hidden;
    &::before {
      content: '';
      position: absolute;
      left: 0;
      right: 50%;
      top: 0;
      bottom: 0;
      background-color: var(--color);
    }
  }
    /* 遮挡转动轴 */
    /* 可能无法完全遮住,需要更大一点 */
  .mask {
    position: absolute;
    width: calc(100% + var(--thickness) + 1px);
    height: calc(100% + var(--thickness) + 1px);
    left: 50%;
    top: 50%;
    transform: translateX(-50%) translateY(-50%);
    border-radius: 50%;
    overflow: hidden;
    /* transition: transform .3s; */
    &::before {
      content: '';
      position: absolute;
      left: 0;
      right: 49.5%;
      top: 0;
      bottom: 0;
      background-color: var(--bg0);
      /* background-color: yellow; */
    }
  }
  /* 转动超过180后显示 */
  .after {
    position: absolute;
    width: calc(100% + var(--thickness));
    height: calc(100% + var(--thickness));
    left: 50%;
    top: 50%;
    transform: translateX(-50%) translateY(-50%) rotate(0);
    border-radius: 50%;
    overflow: hidden;
    &::before {
      content: '';
      position: absolute;
      left: 50%;
      right: 0;
      top: 0;
      bottom: 0;
      background-color: var(--color);
      display: none;
    }
  }
  .content {
    background-color: var(--bg0); //必须覆盖圈的背景色
    border-radius: 50%;
    padding: var(--gap);
    position: relative;//提高层级
  }
}
</style>