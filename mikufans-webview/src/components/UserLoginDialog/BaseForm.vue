<template>
  <div>
    <div class="mode-select">
      <span :class="{active:mode===0}" @click="mode=0">密码登录</span>
      <el-divider direction="vertical" class="h-auto"/>
      <span :class="{active:mode===1}" @click="mode=1">邮箱登录</span>
    </div>
    <login-form v-if="mode===0" @register="mode=1"/>
    <register-form v-else-if="mode===1"/>
    <third-auth />
  </div>
</template>

<script setup lang="ts">
import LoginForm from './LoginForm.vue';
import RegisterForm from './RegisterForm.vue';
import ThirdAuth from './ThirdAuth.vue'
const props = defineProps({
  defMode: number().def(0)
})
const mode = ref(props.defMode)
</script>

<style scoped lang="scss">
.mode-select {
  display: flex;
  justify-content: center;
  gap: 8px;
  user-select: none;
  margin-top: 4px;
  span {
    cursor: pointer;
    &.active {
      color: var(--blue1);
    }
  }
}
</style>