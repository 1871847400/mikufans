<template>
  <el-input 
    type="textarea"
    :rows="data.rows"
    resize="none"
    :maxlength="data.maxlength" 
    :show-word-limit="data.maxlength!=-1" 
    :placeholder="data.placeholder" 
  />
</template>

<script setup lang="ts">
defineProps<{ data: FormItem }>()
</script>

<style scoped lang="scss">
</style>