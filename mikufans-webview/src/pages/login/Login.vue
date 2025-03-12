<template>
  <el-form size="large" :model="data" ref="formRef" :rules="rules">
    <div class="title">MIKUFANS登录页面</div>
    <el-form-item prop="username">
      <el-input v-model="data.username" clearable placeholder="账号(邮箱或昵称)">
        <template #prefix>
          <span class="iconfont">&#xe602;</span>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input v-model="data.password" show-password placeholder="密码" @keyup.enter="onSubmit">
        <template #prefix>
          <span class="iconfont">&#xe65b;</span>
        </template>
      </el-input>
    </el-form-item>
    <el-button type="primary" class="submit" @click="onSubmit">登录</el-button>
    <div class="mt-3 text-xs flex justify-between">
      <el-button link @click="$router.replace('/register')">注册账号</el-button>
      <el-button link @click="openDialog(ForgetDialog)">找回密码</el-button>
    </div>
  </el-form>
</template>

<script setup lang="ts">
import { ElForm } from 'element-plus';
import { useFocusTrap } from '@vueuse/integrations/useFocusTrap';
import { openDialog } from '@/utils/dialog';
import loginApi from '@/apis/security/login';
import ForgetDialog from './ForgetDialog.vue';
import PuzzleCaptcha from '@/components/PuzzleCaptcha/index.vue'
const router = useRouter()
const formRef = ref<InstanceType<typeof ElForm>>()
//让用户通过tab切换焦点时限定在form内
//注意激活后容器外的鼠标事件会被阻止
useFocusTrap(toRef(()=>formRef.value?.$el), { 
  immediate: true,
  allowOutsideClick: true,
  clickOutsideDeactivates: true,
})
const data = reactive({
  username: '',
  password: '',
  puzzleId: '',
})
const rules = {
  username: [{
    required: true, message: '请填写账号', trigger: 'blur'
  }],
  password: [{
    required: true, message: '请填写密码', trigger: 'blur'
  }],
}
const emits = defineEmits(['puzzle', 'forget'])
function onSubmit() {
  formRef.value.validate().then(() => {
    openDialog(PuzzleCaptcha, {
      async onSuccess(puzzleId) {
        data.puzzleId = puzzleId
        await loginApi.login(data)
        await router.replace('/')
        message.success('登录成功！')
      }
    })
  }).catch(()=>{})
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
  padding: 48px 0;
  white-space: nowrap;
  text-align: center;
}
</style>