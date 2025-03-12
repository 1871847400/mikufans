<template>
  <div class="play-section">
    <play-list/>
  </div>
</template>

<script setup lang="ts">
import PlayList from './PlayList.vue';
</script>

<style scoped lang="scss">
.play-section {
  background-color: var(--bg0);
  padding: 16px;
  padding-bottom: 32px;
  border-radius: 8px;
  flex: 1;
}
</style>