<template>
  <el-radio-group v-model="value">
    <!-- <el-radio-button :value="1"></el-radio-button> -->
  </el-radio-group>
</template>

<script setup lang="ts">
import dictApi from '@/apis/admin/dict';
const value = defineModel<string>('modelValue')
</script>

<style scoped lang="scss">

</style>