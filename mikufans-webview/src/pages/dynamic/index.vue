<template>
  <div class="dynamic-page">
    <nav-bar/>
    <div class="bg"></div>
    <dynamic-view v-if="id" :dynamic-id="id" />
    <div v-else class="flex gap-4 py-2 mx-auto w-fit relative">
      <aside class="w-[270px] xl:w-[243px]">
        <user-section/>
      </aside>
      <main class="w-[700px] xl:w-[630px]">
        <user-publish/>
        <follow-list/>
        <dynamic-list/>
      </main>
      <aside class="w-[300px] xl:w-[270px]">
        <topic-index />
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import FollowList from './follow/index.vue';
import DynamicList from './list/index.vue'
import UserPublish from './UserPublish.vue';
import UserSection from './UserSection.vue';
import TopicIndex from './topic/index.vue'
import DynamicView from './DynamicView.vue';
//动态id
const id = useRouteParams('id', '')
</script>

<style lang="scss" scoped>
.dynamic-page {
  min-height: 100vh;
  position: relative;
  .bg {
    width: 100%;
    height: 100%;
    min-height: 100vh;
    background-image: url('@/assets/images/dynamic_bg.png');
    background-repeat: no-repeat;
    background-position: center bottom;
    background-size: cover;
    max-width: 2048px;
    position: fixed;
    top: 0;
    z-index: -1;
  }
  aside > div {
    position: sticky;
    top: calc(var(--navbar-height) + 8px);
  }
}
</style>