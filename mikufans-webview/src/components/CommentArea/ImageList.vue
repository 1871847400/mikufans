<template>
  <div class="flex flex-wrap mt-1 gap-1" v-if="imgIds.length">
    <div v-for="id in imgIds" :key="id" :class="small?'size-16':'size-24'">
      <miku-image :res-id="id" class="size-full object-cover" :preview="imgIds"/>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ imgIds: string[], small?: boolean }>()
</script>