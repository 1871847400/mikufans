<template>
  <button class="text-button" :class="class" type="button">
    <slot></slot>
  </button>
</template>

<script setup lang="ts">
defineProps({
  class: string(), // 提高外部传入class的优先级
})
</script>

<style lang="scss">
.text-button {
  background: none;
  border: none;
  outline: none;
  cursor: pointer;
  user-select: none;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  border-radius: 6px;
  white-space: nowrap;
  transition: color .3s;
  color: inherit; //button默认button-text
  font-family: inherit;
  &:hover {
    color: var(--blue0) !important;
  }
  &:focus {
    
  }
}
</style>