<template>
  <el-select v-model="model" v-bind="select" :empty-values="emptyValues" clearable>
    <el-option v-for="i in options" :label="i.label" :value="i.value+''" />
  </el-select>
</template>

<script setup lang="ts">
import { isNumber } from 'lodash';
const model = defineModel({
  get(v) {
    return isNumber(v) ? v + '' : v
  }
})
const props = defineProps<{ data: FormItem, options: Option[], select: FormSelect }>()
const emptyValues = [null, undefined, '']
if (props.select?.empty) {
  emptyValues.push(...props.select.empty)
}
</script>

<style scoped lang="scss">
</style>