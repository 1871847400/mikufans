<template>
  <div>
    <div class="pb-2">
      <span>{{ title }}</span>
    </div>
    <el-scrollbar max-height="260px">
      <div>
        <template v-for="i in list" :key="i.id">
          <part-item :part="i" list-type :active="videoPart.id===i.id" />
        </template>
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import PartItem from './PartItem.vue';
defineProps({
  title: string(),
  list: array<VideoPart>().def([])
})
const { videoPart } = toRefs(useVideoStore())
</script>

<style scoped lang="scss">
</style>