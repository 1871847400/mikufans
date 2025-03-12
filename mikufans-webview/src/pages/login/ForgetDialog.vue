<template>
  <el-dialog v-model="show" width="600px" title="重置密码" center :close-on-click-modal="false" append-to-body>
    <el-steps :space="200" :active="step" finish-status="success" align-center>
      <el-step title="输入邮箱"/>
      <el-step title="验证邮箱" />
      <el-step title="修改密码" />
      <el-step title="完成" />
    </el-steps>
    <el-form v-if="step<3" class="mt-12 px-[68px]" ref="formRef" size="large" :model="form" :rules="rules" @submit.prevent="submit">
      <el-form-item v-if="step==0" prop="username">
        <el-input v-model="form.username" placeholder="请输入邮箱" clearable autofocus :disabled="!!email">
          <template #prefix>
            <span class="iconfont">&#xe64d;</span>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item v-else-if="step==1" prop="code">
        <el-input v-model="form.code" placeholder="请输入验证码" autofocus>
          <template #prefix>
            <span class="iconfont">&#xeb68;</span>
          </template>
        </el-input>
      </el-form-item>
      <template v-else-if="step==2">
        <el-form-item prop="password">
          <el-input v-model="form.password" show-password placeholder="请输入密码" autofocus>
            <template #prefix>
              <span class="iconfont">&#xe65b;</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password2">
          <el-input v-model="form.password2" show-password placeholder="请再次输入密码" @keyup.enter="submit">
            <template #prefix>
              <span class="iconfont">&#xe65b;</span>
            </template>
          </el-input>
        </el-form-item>
      </template>
      <div class="mb-4 pt-2">
        <el-button class="w-full" type="primary" native-type="submit" :loading="loading">{{ step===2?'完成修改':'下一步' }}</el-button>
        <el-button v-if="step===1" class="w-full mt-2 mr-2" type="info" @click="step=0">返回上一步</el-button>
      </div>
    </el-form>
    <el-result v-else
      icon="success"
      title="已重置密码"
      sub-title="使用新密码进行登录吧"
    >
      <template #extra>
        <el-button type="primary" @click="show=false">关闭</el-button>
      </template>
    </el-result>
  </el-dialog>
</template>

<script setup lang="ts">
import forgetApi from '@/apis/security/forget'
import { openDialog } from '@/utils/dialog';
import { ElForm, FormRules } from 'element-plus'
import PuzzleCaptcha from '@/components/PuzzleCaptcha/index.vue'
const props = defineProps({
  email: string().def('') //如果不为空，则只能使用这个邮箱
})
const show = ref(true)
const formRef = ref<InstanceType<typeof ElForm>>()
const form = reactive({
  username: props.email,
  code: '',
  password: '',
  password2: '',
})
const emailData = reactive({
  uuid: '',
  username: ''
})
const step = ref(0)
const loading = ref(false)
function submit() {
  loading.value = true
  formRef.value.validate().then(async () => {
    if (step.value == 0) {
      if (emailData.uuid && emailData.username===form.username) {
        step.value = 1
      } else {
        openDialog(PuzzleCaptcha, {
          async onSuccess(puzzleId) {
            const data = await forgetApi.sendEmail({
              email: form.username, puzzleId
            })
            emailData.uuid = data.uuid
            emailData.username = form.username
            step.value=1
            message.success('验证邮件已发送到您的邮箱！')
          }
        })
      }
    } else if (step.value == 1) {
      await forgetApi.verifyEmail({ 
        uuid: emailData.uuid, 
        code: form.code,
        email: emailData.username
      })
      step.value=2
      message.success('验证成功！')
    } else if (step.value == 2) {
      await forgetApi.changePassword({ uuid: emailData.uuid, password: form.password })
      step.value=3
      message.success('修改密码完成！')
    }
  }).catch(() => {})
  .finally(() => loading.value = false)
}
const rules = <FormRules>{
  username: [
    { required: true, message: '请填写邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确',  trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请填写密码', trigger: 'blur' },
    { min: 6, max: 24, message: '密码长度6-24位', trigger: 'blur',},
  ],
  password2: { trigger: 'blur', asyncValidator(rule, value) {
      return new Promise((resolve, reject) => {
        if (value !== form.password) {
          reject('两次输入密码不一致')
        } else {
          resolve()
        }
      })
    }
  },
  code: { required: true, message: '请填写验证码', trigger: 'blur' },
}
</script>

<style scoped>
.el-button+.el-button {
  margin-left: 0;
}
</style>