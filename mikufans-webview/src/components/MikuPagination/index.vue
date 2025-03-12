<template>
  <div v-if="page" class="flex justify-center mt-4 mb-12">
    <el-pagination
      hide-on-single-page
      layout="prev, pager, next, total"
      background
      :current-page="page.current"
      :total="page.total"
      :page-size="page.size"
      @current-change="onChange"
    />
  </div>
</template>

<script setup lang="ts">
defineProps<{
  page?: Page<any>
}>()
const pageModel = defineModel('modelValue', { default: 1 })
const emits = defineEmits<{ change: [pageNum: number] }>()
function onChange(pageNum: number) {
  pageModel.value = pageNum
  emits('change', pageNum)
}
</script>

<style scoped lang="scss">
</style>