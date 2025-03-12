<template>
  <div>
    <el-button type="primary" @click="open">修改密码</el-button>
  </div>
</template>

<script setup lang="ts">
import ForgetDialog from '@/pages/login/ForgetDialog.vue';
import { openDialog } from '@/utils/dialog';
const userStore = useUserStore()
function open() {
  openDialog(ForgetDialog, { 
    // email: userStore.username 
  })
}
</script>

<style scoped lang="scss">

</style>