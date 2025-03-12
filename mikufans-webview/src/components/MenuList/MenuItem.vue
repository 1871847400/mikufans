<template>
  <template v-if="route.meta.visible===false"></template>
  <el-sub-menu v-else-if="route.children.length" :index="route.path">
    <template #title>
      <el-icon><component :is="route.meta.icon"/></el-icon>
      {{ menuName }}
    </template>
    <menu-item v-for="i in route.children" :route="i"/>
  </el-sub-menu>
  <el-menu-item v-else :index="route.path">
    <el-icon v-if="route.meta.icon"><component :is="route.meta.icon"/></el-icon>
    <span>{{ menuName }}</span>
  </el-menu-item>
</template>

<script setup lang="ts">
import { RouteRecordRaw } from 'vue-router';
const props = defineProps<{ route: RouteRecordRaw }>()
const menuName = computed(()=>{
  return props.route.meta.title || props.route.name
})
</script>

<style scoped>
</style>