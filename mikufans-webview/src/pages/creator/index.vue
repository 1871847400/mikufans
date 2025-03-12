<template>
  <nav-bar/>
  <main>
    <Sidebar />
    <div class="bg0 my-4 w-[85%] rounded-md mx-4 min-w-[900px]">
      <router-view />
    </div>
  </main>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar/index.vue';
import Sidebar from './sidebar/index.vue';
</script>

<style scoped lang="scss">
main {
  background-color: var(--bg1);
  display: flex;
  justify-content: center;
  position: relative;
  /* margin: 0 auto; */
  padding-left: 190px; //取决于sidebar宽度
  min-height: calc(100vh - var(--navbar-height));
}
</style>