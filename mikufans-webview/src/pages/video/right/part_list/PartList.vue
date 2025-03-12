<template>
  <el-scrollbar max-height="260px">
    <div :class="{'part-list-grid':!listType}">
      <template v-for="i in records" :key="i.id">
        <part-item :video="video" :part="i" :list-type="listType" :active="videoPart?.id===i.id" />
      </template>
    </div>
  </el-scrollbar>
</template>

<script setup lang="ts">
import PartItem from './PartItem.vue';
const { video, listType, reverse, videoPart } = toRefs(useVideoStore())
const records = computed(()=>{
  const result = video.value.parts.filter(a=>a.canplay)
  return reverse.value ? result.reverse() : result
})
</script>

<style scoped lang="scss">
.part-list-grid {
  padding-top: 5px;
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  grid-template-rows: auto;
  gap: 10px;
}
</style>