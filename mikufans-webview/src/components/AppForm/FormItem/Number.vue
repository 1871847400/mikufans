<template>
  <el-input-number
    v-model="model"
    :max="data.max" 
    :min="data.min"
    :placeholder="data.placeholder" 
  />
</template>

<script setup lang="ts">
import { isNumber, toNumber } from 'lodash';
const model = defineModel({
  get(v) {
    return isNumber(v) ? toNumber(v) : 0
  }
})
defineProps<{ data: FormItem }>()
</script>

<style scoped lang="scss">
</style>