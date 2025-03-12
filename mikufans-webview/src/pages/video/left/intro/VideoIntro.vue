<template>
  <section class="video-intro">
    <div v-if="video.republish" class="mb-1">
      <span>转载地址：</span>
      <el-link type="primary" :href="video.sourceUrl" target="_blank">{{ video.sourceUrl }}</el-link>
    </div>
    <rich-text html :content="video.intro" :rows="3" :line-height="2" />
  </section>
</template>

<script setup lang="ts">
const props = defineProps({
  video: object<Video>()
})
</script>

<style scoped lang="scss">
.video-intro {
  margin-top: 16px;
  margin-bottom: 48px;
  min-height: 48px;
  position: relative;
  font-size: 14px;
}
</style>