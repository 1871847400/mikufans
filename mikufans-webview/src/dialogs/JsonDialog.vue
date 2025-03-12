<template>
  <el-dialog v-model="dialog" width="600px" center title="对象预览">
    <el-scrollbar max-height="500px">
      <vue-json-pretty :data="data" />
    </el-scrollbar>
  </el-dialog>
</template>

<script setup lang="ts">
import VueJsonPretty from 'vue-json-pretty';
import 'vue-json-pretty/lib/styles.css';
const dialog = ref(true)
const props = defineProps<{ data: any }>()
</script>

<style scoped lang="scss">
</style>