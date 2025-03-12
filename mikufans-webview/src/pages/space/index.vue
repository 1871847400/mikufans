<template>
  <div class="space-page">
    <nav-bar style="background-color: #fff;"/>
    <div class="space-page-content" v-if="user">
      <space-header :user="user"/>
      <space-menu />
      <div class="active-area" v-if="!loading">
        <router-view v-slot="{ Component }">
          <!-- <keep-alive> -->
            <component :is="Component" style="flex: 1 1 0;"/>
          <!-- </keep-alive> -->
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouteParams } from '@vueuse/router';
import SpaceHeader from './header/index.vue'
import SpaceMenu from './menu/index.vue'
import userApi from '@/apis/user';
const { user, userId } = toRefs(useSpaceStore())
const uid = useRouteParams('uid', '')
const loading = ref(true)
watchEffect(()=>{
  userId.value = uid.value
})
watchImmediate(userId, async ()=>{
  if (userId.value) {
    loading.value = true
    //无论是否为自己都应该重新获取
    user.value = await userApi.fetch(userId.value)
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.space-page {
  min-height: 100vh;
  background-color: var(--bg1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.space-page-content {
  width: 82%;
  min-width: 1000px;
  margin: 0 auto;
  flex: 1;
  display: flex;
  flex-direction: column;
}
.active-area {
  flex: 1;
  background-color: var(--bg0);
  margin: 16px 0;
  box-sizing: border-box;
  display: flex;
  min-height: 400px;
  border-radius: 8px;
}
</style>