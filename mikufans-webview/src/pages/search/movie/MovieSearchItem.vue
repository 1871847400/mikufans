<template>
  <anime-search-item class="movie-search-item" :video="video" />
</template>

<script setup lang="ts">
import AnimeSearchItem from '../anime/AnimeSearchItem.vue';
defineProps<{ video: Video }>()
</script>

<style scoped lang="scss">
</style>