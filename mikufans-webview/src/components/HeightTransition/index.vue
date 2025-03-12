<template>
  <div class="h-anim" :class="{ expand }">
    <!-- 内层必须包装一个div做高度动画 -->
    <div>
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps({ 
  expand: bool().def(true)
})
</script>

<style scoped lang="scss">
/* height:auto不支持过渡动画 */
.h-anim {
  transition: grid .3s;
  display: grid;
  overflow: hidden;
  grid-template-rows: 0fr;
  & > div {
    min-height: 0;
  }
  &.expand {
    grid-template-rows: 1fr;
  }
}
</style>