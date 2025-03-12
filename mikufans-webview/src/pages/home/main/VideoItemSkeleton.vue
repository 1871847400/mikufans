<template>
  <el-skeleton animated>
    <template #template>
      <el-skeleton-item class="aspect-video w-[100%] h-auto" variant="image" style="border-radius: 8px;"/>
      <div style="margin-top: 6px;display: flex;">
        <el-skeleton-item variant="circle" style="width: 40px;height: 40px;"/>
        <div style="flex: 1;padding: 0 6px;">
          <el-skeleton-item variant="h1"/>
          <el-skeleton-item variant="p" style="width: 40%;" />
          <div style="display: flex; justify-content: space-between;">
            <el-skeleton-item variant="caption" style="width: 25%"/>
            <el-skeleton-item variant="caption" style="width: 20%"/>
          </div>
        </div>
      </div>
    </template>
  </el-skeleton>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
</style>