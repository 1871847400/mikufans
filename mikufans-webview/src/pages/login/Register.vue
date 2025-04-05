<template>
  <el-form size="large" :model="form" ref="formRef" :rules="rules">
    <div class="title">MIKUFANS注册页面</div>
    <el-form-item prop="user.nickname">
      <el-input v-model="form.nickname" clearable placeholder="昵称">
        <template #prefix>
          <span class="iconfont">&#xe678;</span>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="email">
      <el-input v-model="form.email" clearable placeholder="邮箱">
        <template #prefix>
          <span class="iconfont">&#xe64d;</span>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="user.password">
      <el-input v-model="form.password" show-password placeholder="6-20位密码">
        <template #prefix>
          <span class="iconfont">&#xe65b;</span>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="password2">
      <el-input v-model="form.password2" show-password placeholder="再次输入密码">
        <template #prefix>
          <span class="iconfont">&#xe65b;</span>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="code">
      <el-input v-model="form.code" placeholder="邮箱验证码">
        <template #prefix>
          <span class="iconfont">&#xeb68;</span>
        </template>
        <template #append>
          <el-button @click="sendEmailCode" :loading="sending" :disabled="coolDown>0">{{ coolDown>0 ? coolDown + 's' : '发送验证码' }}</el-button>
        </template>
      </el-input>
    </el-form-item>
    <el-button type="primary" class="submit" @click="register">注册</el-button>
    <div class="text-xs mt-3">
      <el-button link @click="$router.replace('/login')">已有账号，我要登录！</el-button>
    </div>
  </el-form>
</template>

<script setup lang="ts">
import router from '@/router';
import { ElForm, FormRules } from 'element-plus';
import loginApi from '@/apis/security/login';
import { useFocusTrap } from '@vueuse/integrations/useFocusTrap';
import PuzzleCaptcha from '@/components/PuzzleCaptcha/index.vue'
import { openDialog } from '@/utils/dialog';
const formRef = ref<InstanceType<typeof ElForm>>()
useFocusTrap(toRef(()=>formRef.value?.$el), { 
  immediate: true,
  allowOutsideClick: true,
  clickOutsideDeactivates: true,
})
const form = reactive<EmailLoginDto & { password2: string }>({
  uuid: '',
  email: '',
  code: '',
  nickname: '',
  password: '',
  password2: ''
})
const coolDown = ref(0)
const sending = ref(false)
useInterval(1000, {callback() {
  if (coolDown.value>0) {
    coolDown.value--
  }
},})
function sendEmailCode() {
  formRef.value.validateField('email').then(()=>{
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
  }).catch(() => {
    message.warning('请先填写邮箱字段')
  })
}
function register() {
  formRef.value.validate().then(async () => {
    await loginApi.emailLogin(toRaw(form))
    await router.replace('/')
    message.success('登录/注册成功！')
  }).catch(()=>{})
}
const rules = <FormRules>{
  nickname: [
    { required: true, message: '请填写昵称', trigger: 'blur', whitespace: true,}, 
    { min: 2, max: 16, message: '昵称长度2-16位', trigger: 'blur',},
  ],
  email: [
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
  code: [
    { required: true, message: '请填写验证码', trigger: 'blur' },
    { trigger: 'blur', asyncValidator() {
      return new Promise((resolve, reject) => {
        if (!form.uuid) {
          reject('请先点击发送验证码')
        } else {
          resolve()
        }
      })
    }}
  ],
}
</script>

<style scoped lang="scss">
.submit {
  width: 100%;
  font-size: 16px;
  letter-spacing: 4px;
}
.title {
  font-size: 24px;
  font-weight: 550;
  letter-spacing: 2px;
  padding: 36px 0;
  white-space: nowrap;
  text-align: center;
}
</style>