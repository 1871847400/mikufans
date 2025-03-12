<template>
  <!-- el-popover将不会处理dom的属性,例如class -->
  <user-popover :popover="popover" :user="__user">
    <router-link v-bind="$attrs" @click="onClick" :to="path" target="_blank" class="user-avatar h-fit" draggable="false">
      <miku-image :class="imgClass" :res-id="__user.avatarId" avatar draggable="false"/>
    </router-link>
  </user-popover>
</template>

<script setup lang="ts">
import { CSSProperties } from 'vue';
const props = defineProps({
  user: object<BaseEntity['user']>(), //用户数据,默认自己
  popover: bool().def(true), //鼠标悬浮显示信息
  size: string().def('60px'),
  imgClass: object<CSSProperties>()
})
const userStore = useUserStore()
const __user = computed(()=>props.user ?? userStore.$state)
const path = computed(()=>{
  if (props.user || userStore.isLogin) {
    return '/space/' + __user.value.id
  }
  return '#'
})
function onClick(e: MouseEvent) {
  if (!props.user && !userStore.isLogin) {
    e.preventDefault()
    userStore.login()
  }
}
</script>

<style scoped lang="scss">
.miku-image {
  width: v-bind(size);
  height: v-bind(size);
}
</style>