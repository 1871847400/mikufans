<template>
  <div class="video-page-right">
    <author-header/>
    <danmu-index />
    <part-index />
    <relate-list />
  </div>
</template>

<script setup lang="ts">
import AuthorHeader from './header/index.vue';
import DanmuIndex from './danmu/index.vue';
import PartIndex from './part_list/index.vue';
import RelateList from './relate/index.vue'
</script>