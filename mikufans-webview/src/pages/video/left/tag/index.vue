<template>
  <div class="index" v-if="video.tags">
    <template v-for="tag in video.tags" :key="tag">
      <span class="video-tag" @click="search(tag)">{{ tag }}</span>
    </template>
  </div>
</template>

<script setup lang="ts">
const { video } = toRefs(useVideoStore())
function search(tag: string) {
  window.open(`/search/all?kw=${tag}`, '_blank')
}
</script>

<style scoped lang="scss">
.index {
  margin-bottom: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  .video-tag {
    font-size: 14px;
    cursor: pointer;
    background-color: var(--bg1);
    border-radius: 8px;
    padding: 8px 12px;
  }
}
</style>