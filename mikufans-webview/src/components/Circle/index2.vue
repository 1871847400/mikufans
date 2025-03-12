<template>
  <div class="circle" :class="{ half }" :style="{'--deg':deg + 'deg', '--radius': radius}">
    <div class="before"></div>
    <div class="mask"></div>
    <div class="after"></div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  radius: string().def('42px'),
  anim: bool().def(true),
})
const value = defineModel<number>('modelValue', { default: 0 })
const emits = defineEmits<{ change: [val: number] }>()
const deg = ref(0)
useIntervalFn(()=>{
  if (!props.anim) {
    if (deg.value !== value.value) {
      deg.value = value.value
      emits('change', deg.value)
    }
  } else if (deg.value < value.value) {
    deg.value = Math.min(deg.value + 3, value.value)
    emits('change', deg.value)
  } else if (deg.value > value.value) {
    deg.value = Math.max(deg.value - 3, value.value)
    emits('change', deg.value)
  }
}, 10)
//已经转动半圈
const half = computed(()=>deg.value>=180)
</script>

<style scoped lang="scss">
.circle {
  --color: var(--pink0);
  /* 作为mask遮挡转动轴,需要更大一点,否则无法遮挡 */
  .mask {
    position: absolute;
    width: calc(var(--radius) + 2px);
    height: calc(var(--radius) + 2px);
    left: 50%;
    top: 50%;
    transform: translateX(-50%) translateY(-50%) rotate(0);
    border-radius: 50%;
    overflow: hidden;
    /* transition: transform .3s; */
    z-index: -1;
    &::before {
      content: '';
      position: absolute;
      left: 0;
      right: 50%;
      top: 0;
      bottom: 0;
      background-color: var(--bg0);
    }
  }
  &.half .mask::before {
    background: none;
    /* transform: translateX(-50%) translateY(-50%) rotate(180deg); */
  }
  &.half .after::before {
    display: block;
  }
  
  /* 作为转动轴 */
  .before {
    position: absolute;
    width: var(--radius);
    height: var(--radius);
    left: 50%;
    top: 50%;
    transform: translateX(-50%) translateY(-50%) rotate(var(--deg));
    /* transition: transform .3s; */
    border-radius: 50%;
    overflow: hidden;
    z-index: -1;
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
  /* 转动超过180后显示 */
  .after {
    position: absolute;
    width: var(--radius);
    height: var(--radius);
    left: 50%;
    top: 50%;
    transform: translateX(-50%) translateY(-50%) rotate(0);
    border-radius: 50%;
    overflow: hidden;
    z-index: -1;
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
}
</style>