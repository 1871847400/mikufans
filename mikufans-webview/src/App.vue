<template>
  <el-config-provider :locale="zhCn" :message="config">
    <router-view />
    <bottom-bar />
  </el-config-provider>
</template>

<script setup lang="ts">
import { MessageConfigContext } from 'element-plus';
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
useMsgStore() //在APP.vue注册socket事件
const config : MessageConfigContext = {
  max: 3, //最多同时显示N条ElMessage
}
// console.log(window.innerWidth, window.outerWidth); //1536 1536
</script>

<style>
#app {
  width: 100%;
  min-height: 100vh;
}
</style>