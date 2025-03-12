<template>
  <router-link :to="to" :class="isActive ? activeClass : {}">
    <slot></slot>
  </router-link>
</template>

<script setup lang="ts">
import { RouterLink, useLink, RouteLocationRaw } from 'vue-router'
import { CSSProperties } from 'vue';
const props = defineProps({
  ...RouterLink.props, /* @ts-ignore */
})
const {
  // 解析出来的路由对象
  route,
  // 用在链接里的 href
  href,
  // 布尔类型的 ref 标识链接是否匹配当前路由
  isActive,
  // 布尔类型的 ref 标识链接是否严格匹配当前路由
  isExactActive,
  // 导航至该链接的函数
  navigate
} = useLink(props)
</script>

<style scoped lang="scss">
.router-link-active {

}
</style>