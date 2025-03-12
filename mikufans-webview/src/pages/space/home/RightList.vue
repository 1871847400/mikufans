<template>
  <div class="w-[280px]">
    <div class="card">
      <div class="text-lg">个人资料</div>
      <el-divider class="my-4"/>
      <div class="flex gap-8">
        <div>UID: {{ user.id }}</div>
        <div>生日: {{ user.birthday || '保密' }}</div>
      </div>
    </div>
    <div class="card">
      <div class="text-lg">公告</div>
      <el-divider class="my-4"/>
      <div>暂无</div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { user } = toRefs(useSpaceStore())
</script>

<style scoped lang="scss">
.card {
  background-color: var(--bg0);
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 16px;
}
</style>