<template>
  <el-radio-group v-model="model">
    <template v-for="{ label, value } in options">
      <el-radio :label="label" :value="value+''" />
    </template>
  </el-radio-group>
</template>

<script setup lang="ts">
import { isNumber } from 'lodash';
const model = defineModel({
  get(v: any) {
    //转为string防止选项可能是数字可能是stirng
    return isNumber(v) ? v + '' : v
  }
})
defineProps<{ data: FormItem, options: Option[] }>()
</script>

<style scoped lang="scss">
</style>