<template>
  <el-date-picker 
    type="datetime" 
    :value-format="data.timeFormat"
    :format="data.timeFormat" 
    :placeholder="data.placeholder" 
  />
</template>

<script setup lang="ts">
defineProps<{ data: FormItem }>()
</script>

<style scoped lang="scss">

</style>