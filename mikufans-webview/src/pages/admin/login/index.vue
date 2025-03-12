<template>
  <div class="h-[100vh] flex items-center justify-center bg2">
    <el-form 
      ref="form" 
      size="large" 
      class="w-[400px] p-5 bg0 rounded-lg" 
      :model="formData" 
      :rules="formRules" 
      @submit.prevent="submit"
    >
      <div class="text-center text-xl mb-5">Mikufans后台管理系统</div>
      <el-form-item prop="username">
        <el-input v-model="formData.username" prefix-icon="user" placeholder="账号"/>
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="formData.password" prefix-icon="lock" placeholder="密码" show-password/>
      </el-form-item>
      <el-form-item prop="captcha">
        <el-input ref="captcha" v-model="formData.captcha" prefix-icon="circle-check" placeholder="验证码" class="flex-1 mr-4"/>
        <el-image :src="imageData" @click="getImage" class="cursor-pointer w-[120px] h-[38px]" title="刷新"/>
      </el-form-item>
      <el-button type="primary" class="w-full" native-type="submit" :loading="submitting">登录</el-button>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { getCaptcha, login } from '@/apis/admin/login';
import { ElForm, ElInput } from 'element-plus';
const sysUserStore = useSysUserStore()
sysUserStore.logout()
const router = useRouter()
const form = ref<InstanceType<typeof ElForm>>()
const captcha = ref<InstanceType<typeof ElInput>>()
const formData = reactive({
  username: '',
  password: '',
  uuid: '',
  captcha: '',
})
const formRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
}
const imageData = ref('')
async function getImage() {
  const data = await getCaptcha()
  formData.captcha = ''
  captcha.value.focus()
  formData.uuid = data.uuid
  imageData.value = data.image
}
getImage()
const submitting = ref(false)
async function submit() {
  try {
    submitting.value = true
    await form.value.validate()
    await login(formData)
    router.push('/admin/home')
  } catch (err) {
    getImage()
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
</style>