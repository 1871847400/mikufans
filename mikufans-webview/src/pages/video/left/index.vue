<template>
  <div class="video-page-left">
    <video-header />
    <video-player />
    <action-bar />
    <video-intro v-if="video.type=='VIDEO'" :video="video"/>
    <bangumi-intro v-else :bangumi="video.bangumi" />
    <tag-list />
    <el-divider class="my-2"/>
    <comment-area
      :key="video.id"
      :area="video.dynamic.commentArea"
      use-hash
    />
  </div>
</template>

<script setup lang="ts">
import VideoHeader from './header/index.vue'
import ActionBar from './actionbar/index.vue';
import TagList from './tag/index.vue'
import VideoIntro from './intro/VideoIntro.vue';
import BangumiIntro from './intro/BangumiIntro.vue';
const { video } = toRefs(useVideoStore())
</script>

<style scoped lang="scss">
.video-page-left {
  padding-top: 24px;
  padding-bottom: 64px;
}
</style>