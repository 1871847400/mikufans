<template>
  <el-input 
    type="password"
    show-password
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