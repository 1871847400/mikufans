<template>
  <header>
    <nav-bar transparent virtual/>
    <home-banner />
  </header>
  <main>
    <channel-list />
    <center-section />
    <div class="mb-16"></div>
    <main-section />
  </main>
</template>

<script setup lang="ts">
import HomeBanner from './banner/index.vue'
import ChannelList from './channel/index.vue'
import CenterSection from './center/index.vue';
import MainSection from './main/index.vue';
</script>

<style scoped lang="scss">
main {
  padding: 0 60px;
}
</style>