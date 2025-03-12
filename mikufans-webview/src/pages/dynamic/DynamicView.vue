<template>
  <div v-if="isReady" class="w-1/2 mx-auto mt-2">
    <dynamic-item :data="state" class="z-20" />
  </div>
</template>

<script setup lang="ts">
import userDynamicApi from '@/apis/user/dynamic';
import DynamicItem from './list/DynamicItem.vue';
const props = defineProps<{ dynamicId: string }>()
const { state, isLoading, isReady } = useAsyncState(userDynamicApi.getById(props.dynamicId), null)
</script>

<style scoped lang="scss">
</style>