<template>
  <el-form size="large" ref="formRef" @submit.prevent="onSubmit">
    <div class="border-box">
      <el-input clearable v-model="form.username" placeholder="请输入邮箱或昵称">
        <template #prefix>账号</template>
      </el-input>
      <el-input show-password type="password" v-model="form.password" placeholder="请输入密码">
        <template #prefix>密码</template>
        <template #append>
          <el-button type="primary" @click="openDialog(ForgetDialog)">
            <span class="blue1">忘记密码?</span>
          </el-button>
        </template>
      </el-input>
    </div>
    <div class="button-list">
      <el-button type="default" @click="$emit('register')">注册</el-button>
      <el-button type="primary" native-type="submit" :disabled="!form.username||!form.password">登录</el-button>
    </div>
  </el-form>
</template>

<script setup lang="ts">
import { ElForm } from 'element-plus';
import { useFocusTrap } from '@vueuse/integrations/useFocusTrap';
import loginApi from '@/apis/security/login';
import PuzzleCaptcha from '@/components/PuzzleCaptcha/index.vue'
import ForgetDialog from '@/pages/login/ForgetDialog.vue';
import { openDialog } from '@/utils/dialog';
const formRef = ref<InstanceType<typeof ElForm>>()
//让用户通过tab切换焦点时限定在form内
//注意激活后容器外的鼠标事件会被阻止
useFocusTrap(toRef(()=>formRef.value?.$el), { 
  immediate: true,
  allowOutsideClick: true,
  clickOutsideDeactivates: true,
})
const form = reactive({
  username: '',
  password: '',
  puzzleId: '',
})
const emits = defineEmits(['register'])
function onSubmit() {
  openDialog(PuzzleCaptcha, {
    async onSuccess(puzzleId) {
      form.puzzleId = puzzleId
      await loginApi.login(form)
      message.success('登录成功')
      location.reload()
    }
  })
}
</script>

<style scoped lang="scss">
.border-box {
  border: 1px solid #e3e3e3;
  border-radius: 8px;
  margin: 16px 0;
  display: flex;
  flex-direction: column;
  .el-input {
    width: 380px;
  }
  :deep(.el-input__prefix-inner) {
    padding-right: 15px;
  }
  :deep(.el-input__wrapper) {
    box-shadow: none !important;
    background-color: transparent;
  }
  :deep(.el-input-group__append) {
    box-shadow: none !important;
    background-color: transparent;
    padding-left: 0;
  }
  .el-input:not(:last-of-type) {
    border-bottom: 1px solid #e3e3e3;
  }
}
.button-list {
  display: flex;
  margin-top: 16px;
  .el-button {
    flex: 1;
  }
}
</style>