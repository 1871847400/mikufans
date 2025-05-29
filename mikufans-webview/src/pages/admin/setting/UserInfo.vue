<template>
  <div class="p-4 bg0 w-fit rounded-md transition-all shadow-sm hover:shadow-lg">
    <div class="flex items-center gap-8">
      <miku-image class="size-[100px] mr-1 object-cover" :res-id="sysUserStore.avatarId" avatar />
      <div>
        <h1>{{ sysUserStore.username }}</h1>
        <ul class="py-2">
          <li v-if="sysUserStore.id==='1'">超级管理员</li>
          <li v-for="role in sysUserStore.roles" :key="role">{{ role }}</li>
        </ul>
      </div>
    </div>
    <el-divider />
    <div class="mb-2 grey2">上次登录时间：{{ sysUserStore.updateTime }}</div>
    <div class="grey2">上次登录地点：{{ '未知' }}</div>
  </div>
</template>

<script setup lang="ts">
const sysUserStore = useSysUserStore()
</script>

<style scoped lang="scss">

</style>