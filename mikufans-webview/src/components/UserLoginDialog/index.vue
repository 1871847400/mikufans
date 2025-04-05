<template>
  <el-dialog v-model="visible" width="fit-content">
    <div class="dialog-body">
      <div class="flex gap-14">
        <dialog-left />
        <base-form :def-mode="mode"/>
      </div>
      <div class="flex flex-col gap-1 mt-4 text-sm grey2">
        <div class="text-center">未注册的邮箱账号，将会自动注册！</div>
        <div class="text-center">
          <span>登录或完成注册代表你同意</span>
          <a href="/protocol/licence" target="_blank">用户协议</a>
          <span>和</span>
          <a href="/protocol/privacy" target="_blank">隐私政策</a>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import DialogLeft from './DialogLeft.vue'
import BaseForm from './BaseForm.vue';
const props = defineProps({
  mode: number().def(0)
})
const visible = ref(true)
</script>

<style scoped lang="scss">
.dialog-body {
  width: fit-content;
  margin: 0 auto;
  padding: 0 60px;
}
a {
  color: var(--blue0);
  padding: 0 2px;
}
</style>