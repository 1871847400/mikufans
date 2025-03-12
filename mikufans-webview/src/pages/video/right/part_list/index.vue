<template>
  <div>
    <section>
      <top-bar />
      <series-list />
      <part-list />
    </section>
    <!-- <section v-if="video.parts.PV.length">
      <sub-part-list title="PV" :list="video.parts.PV" />
    </section>
    <section v-if="video.parts.SP.length">
      <sub-part-list title="其它" :list="video.parts.SP" />
    </section> -->
  </div>
</template>

<script setup lang="ts">
import PartList from './PartList.vue'
import SeriesList from './SeriesList.vue';
import SubPartList from './SubPartList.vue';
import TopBar from './TopBar.vue';
const { video, videoPart } = toRefs(useVideoStore())
</script>

<style scoped lang="scss">
section {
  background-color: var(--bg1);
  border-radius: 10px;
  padding: 16px 8px;
  margin-bottom: 16px;
  font-size: 14px;
}
</style>