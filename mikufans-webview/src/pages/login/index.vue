<template>
  <div class="login-page">
    <div class="login-box">
      <img :src="Bg" class="object-cover" @load="load=true">
      <router-link to="/" title="回到首页">
        <div class="iconfont home">&#xe60a;</div>
      </router-link>
      <div class="px-10 flex-1" v-if="load">
        <login v-if="$route.name=='login'"/>
        <register v-else/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Bg from '@/assets/images/login_bg.jpg'
import Login from './Login.vue';
import Register from './Register.vue';
const load = ref(false) //图片加载会比较慢,加载完后才显示表单
</script>

<style scoped lang="scss">
.login-page {
  background-color: var(--bg1);
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-box {
  width: 60%;
  min-width: 900px;
  aspect-ratio: 16 / 9;
  height: auto;
  box-shadow: 0 2px 16px rgba(0, 0, 0, .15);
  position: relative;
  border-radius: 6px;
  overflow: hidden;
  background-color: var(--bg0);
  display: flex;
}
.home {
  position: absolute;
  left: 6px;
  top: 6px;
  font-size: 32px;
  color: #fff;
}
</style>