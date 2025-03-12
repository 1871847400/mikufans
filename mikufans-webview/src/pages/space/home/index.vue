<template>
  <div class="index">
    <div class="left">
      <video-show/>
      <work-show v-if="isAllowed('PUBLIC_UPLOAD')" />
      <favor-show/>
    </div>
    <right-list/>
  </div>
</template>

<script setup lang="ts">
import WorkShow from './work/index.vue';
import VideoShow from './represent/index.vue';
import RightList from './RightList.vue';
import FavorShow from './favor/index.vue';
const { isAllowed } = useSpaceStore()
</script>

<style scoped lang="scss">
.index {
  display: flex;
  background-color: var(--bg1);
  gap: 16px;
  .left {
    background-color: var(--bg0);
    border-radius: 8px;
    flex: 1;
    padding: 16px;
  }
}
</style>