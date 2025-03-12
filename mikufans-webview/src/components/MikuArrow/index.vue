<template>
  <span @click="change" class="cursor-pointer p-2">
    <i class="iconfont icon-jiantoushang text-xs" :class="{active}"></i>
  </span>
</template>

<script setup lang="ts">
const active = defineModel('modelValue')
function change() {
  active.value = !active.value
}
</script>

<style scoped lang="scss">
i {
  display: inline-block;
  transition: rotate .3s;
  &.active {
    rotate: 180deg;
  }
}
</style>