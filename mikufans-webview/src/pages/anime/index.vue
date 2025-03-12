<template>
  <div>
    <nav-bar transparent virtual/>
    <banner/>
    <main>
      <Index/>
      <chase v-if="userStore.isLogin"/>
      <season/>
      <favor/>
    </main>
  </div>
</template>

<script setup lang="ts">
import Banner from './banner/index.vue';
import Favor from './favor/index.vue';
import Chase from './chase/index.vue'
import Season from './season/index.vue'
import Index from './index/index.vue'
const userStore = useUserStore()
</script>

<style scoped lang="scss">
main {
  padding: 32px 64px;
  display: flex;
  flex-direction: column;
  row-gap: 50px;
}
</style>