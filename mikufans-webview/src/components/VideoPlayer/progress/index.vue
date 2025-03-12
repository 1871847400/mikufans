<template>
  <media-progress
    v-if="videoPart"
    :video="videoElement" 
    :vtt-url="getThumnailsVttUrl(videoPart.id)" 
    :img-url="getThumnailsImgUrl(videoPart.id)"
  />
</template>

<script setup lang="ts">
import videoResApi from '@/apis/video/resource';
const { getThumnailsVttUrl, getThumnailsImgUrl } = videoResApi
const { videoPart, videoElement, hls } = toRefs(useVideoStore())
</script>

<style scoped lang="scss">
</style>