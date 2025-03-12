<template>
  <ul class="router-menu">
    <router-link v-for="i in items" :to="i.to" class="router-link" active-class="active">
      <slot :item="i">{{ i }}</slot>
    </router-link>
  </ul>
</template>

<script setup lang="ts" generic="T extends MenuItem">
import { RouteLocationRaw } from 'vue-router';
export interface MenuItem {
  to: RouteLocationRaw
}
defineProps({
  items: array<T>()
})
</script>

<style scoped lang="scss">
ul {
}
.router-link {
  display: block;
  width: 160px;
  height: 56px;
  line-height: 56px;
  padding: 0 20px;
  font-size: 14px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  transition: all .3s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  user-select: none;
  &:hover {
    background-color: #ecf5ff;
  }
  &.active {
    background-color: var(--blue0);
    color: #fff;
  }
}
</style>