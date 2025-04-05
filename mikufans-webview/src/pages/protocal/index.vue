<template>
  <div class="px-[100px]">
    <div class="text-center py-10">
      <h1>Mikufans弹幕网{{ route.meta.title }}</h1>
    </div>
    <div ref="container"></div>
  </div>
</template>

<script setup lang="ts">
import { marked } from 'marked'
const container = ref<HTMLDivElement>()
const route = useRoute()
onMounted(async ()=>{
  container.value.innerHTML = await marked.parse('- 项目1 \n - 项目2 \n - 项目3');
})
</script>

<style scoped lang="scss">

</style>