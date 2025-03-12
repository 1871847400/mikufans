<template>
  <div class="center-section">
    <Left/>
    <Right :page-size="pageSize"/>
  </div>
</template>

<script setup lang="ts">
import Left from './left.vue';
import Right from './right.vue';
const { width } = useWindowSize()
const pageSize = computed(()=>width.value<1300?4:6)
const columns = computed(()=>pageSize.value/2+2)
</script>

<style scoped lang="scss">
.center-section {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(v-bind(columns), 1fr);
  grid-template-rows: repeat(2, 1fr);
  position: relative;
}
</style>