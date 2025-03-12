<template>
  <div class="index">
    <user-star-menu />
    <user-star-content />
  </div>
</template>

<script setup lang="ts">
import UserStarMenu from './menu/index.vue';
import UserStarContent from './content/index.vue'
import { useProvide } from './store';
useProvide()
</script>

<style scoped lang="scss">
.index {
  display: flex;
  box-sizing: border-box;
  overflow: hidden;
  padding-top: 12px;
  padding-bottom: 40px;
}
</style>