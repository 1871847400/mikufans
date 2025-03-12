<template>
  <div class="index">
    <play-section/>
    <div class="flex flex-col gap-4 w-[25%]">
      <staff-card v-if="video.bangumi.official" title="官方情报" :content="video.bangumi.official" />
      <staff-card title="角色配音" :content="video.bangumi.voice" />
      <staff-card title="STAFF" :content="video.bangumi.staff" />
    </div>
  </div>
</template>

<script setup lang="ts">
import PlaySection from './PlaySection.vue';
import StaffCard from './StaffCard.vue';
import { useBangumiStore } from '@/stores/bangumi';
const { video } = toRefs(useBangumiStore())
</script>

<style scoped lang="scss">
.index {
  display: flex;
  gap: 8px;
  min-height: inherit;
}
</style>