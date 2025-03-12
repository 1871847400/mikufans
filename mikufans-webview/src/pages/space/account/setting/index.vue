<template>
  <div class="setting-section" v-loading="isLoading">
    <el-switch
      v-if="isReady"
      v-for="flag in state" 
      v-model="flags[flag.name]"
      active-value="true" 
      inactive-value="false"
      :inactive-text="flag.display" 
      @change="onChange(flag.name)"
    />
  </div>
</template>

<script setup lang="ts">
import userFlagApi from '@/apis/user/flag'
const { flags } = toRefs(useUserStore())
const { state, isLoading, isReady } = useAsyncState(userFlagApi.listUserFlags(), null)
function onChange(key: string) {
  userFlagApi.saveUserFlag({
    flagKey: key as USER_FLAGS,
    flagValue: flags.value[key]
  })
}
</script>

<style scoped lang="scss">
.setting-section {
  min-height: 300px;
  display: flex;
  row-gap: 16px;
  flex-direction: column;
  :deep(.el-switch__label) {
    width: 100px;
  }
}
</style>