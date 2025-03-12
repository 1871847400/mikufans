<template>
  <el-scrollbar :max-height="maxHeight">
    <slot name="empty" v-if="empty">
      <div class="text-center py-6">数据为空</div>
    </slot>
    <div 
      v-else
      v-infinite-scroll="next"
      :infinite-scroll-disabled="loading"
      :infinite-scroll-distance="30"
      :infinite-scroll-delay="200"
      :infinite-scroll-immediate="false"
    >
      <slot></slot>
      <div v-if="loading || error" class="text-center py-6">
        <template v-if="loading">加载中...</template>
        <template v-else>加载失败！</template>
      </div>
    </div>
  </el-scrollbar>
</template>

<script setup lang="ts">
defineProps({
  next: func(),
  empty: bool(),
  loading: bool(),
  error: any(),
  maxHeight: string(),
})
</script>

<style scoped lang="scss">

</style>