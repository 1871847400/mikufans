<template>
  <div class="flex gap-6">
    <template v-for="{ label, options, query }, index in state">
      <index-section :color="colors[index%colors.length]" :title="label" :query="query" :options="options" />
    </template>
  </div>
</template>

<script setup lang="ts">
import IndexSection from './IndexSection.vue';
import bangumiApi from '@/apis/bangumi';
const { state } = useAsyncState(bangumiApi.animeFastLinks(), [])
const colors = [
  "#fef3fd",
  "#f1fcf7",
  "#f1f8ff",
  "#fff2f2"
]
</script>

<style scoped lang="scss">
</style>