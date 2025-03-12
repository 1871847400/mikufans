<template>
  <router-menu :items="list">
    <template v-slot="{ item }">
      <span>{{ item.name }}</span>
      <span class="ml-auto">{{ displayNumber(item.count) }}</span>
    </template>
  </router-menu>
</template>

<script setup lang="ts">
import { displayNumber } from '@/utils/common';
const { user, userId } = toRefs(useSpaceStore())
const list = computed(()=>[
  {
    name: '全部关注', 
    count: user.value.follows, 
    to: `/space/${userId.value}/follow`
  },
  {
    name: '全部粉丝', 
    count: user.value.fans, 
    to: `/space/${userId.value}/fans`
  }
])
</script>

<style scoped lang="scss">
</style>