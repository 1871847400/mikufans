<template>
  <el-form size="large" ref="formRef" @submit.prevent="onSubmit">
    <div class="border-box">
      <el-input clearable v-model="form.email" placeholder="请输入邮箱">
        <template #prefix>邮箱</template>
      </el-input>
      <el-input clearable v-model="form.code" placeholder="请输入验证码">
        <template #prefix>验证码</template>
        <template #append>
          <el-button type="primary" @click="sendEmailCode" :disabled="coolDown>0||!isEmail">
            <span v-if="coolDown<=0" class="blue1">发送验证码</span>
            <span v-else class="grey2">重新发送({{ coolDown }})</span>
          </el-button>
        </template>
      </el-input>
    </div>
    <div class="mt-4">
      <el-button 
        type="primary" 
        class="w-full" 
        native-type="submit"
        :disabled="!form.email||!form.code"
      >
        登录&nbsp;/&nbsp;注册
      </el-button>
    </div>
  </el-form>
</template>

<script setup lang="ts">
import { ElForm } from 'element-plus';
import loginApi from '@/apis/security/login';
import { useFocusTrap } from '@vueuse/integrations/useFocusTrap';
import PuzzleCaptcha from '@/components/PuzzleCaptcha/index.vue'
import { validateEmail } from '@/utils/common';
import { openDialog } from '@/utils/dialog';
const formRef = ref<InstanceType<typeof ElForm>>()
useFocusTrap(toRef(()=>formRef.value?.$el), { 
  immediate: true,
  allowOutsideClick: true,
  clickOutsideDeactivates: true,
})
const form = reactive<EmailValidate>({
  uuid: '',
  email: '',
  code: '',
})
const isEmail = computed(()=>validateEmail(form.email))
const coolDown = ref(0)
const sending = ref(false)
useInterval(1000, {callback() {
  if (coolDown.value>0) {
    coolDown.value--
  }
},})
function sendEmailCode() {
  openDialog(PuzzleCaptcha, {
    async onSuccess(puzzleId) {
      sending.value = true
      try {
        const data = await loginApi.sendEmailCode({
          email: form.email, 
          puzzleId,
        })
        form.uuid = data.uuid
        coolDown.value = data.interval
        const minute = (data.timeout/60).toFixed(0)
        message.success('验证邮件已发送到您的邮箱，' + minute + '分钟内有效！')
      } finally {
        sending.value = false
      }
    }
  })
}
async function onSubmit() {
  await loginApi.emailLogin(form)
  message.success('登录成功！')
  location.reload()
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
    width: 3em;
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
</style>