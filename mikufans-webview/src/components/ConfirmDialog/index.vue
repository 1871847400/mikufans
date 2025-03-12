<template>
  <el-dialog class="confirm-dialog" v-model="show" width="400px" :show-close="false">
    <template #header>
      <span class="text-lg">确认提示</span>
      <i class="iconfont cursor-pointer font-bold p-1" @click="show=false">&#xe647;</i>
    </template>
    <el-divider class="m-0"/>
    <div class="flex justify-center items-center px-2 py-6 flex-wrap overflow-hidden">
      <div class="message">{{ message }}</div>
    </div>
    <template #footer>
      <el-button size="large" type="primary" @click="submit(true)">确定</el-button>
      <el-button size="large" @click="submit(false)">取消</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
defineProps<{ message: string }>()
const emits = defineEmits<{ submit: [confirm: boolean] }>()
const show = ref(true)
function submit(confirm: boolean) {
  emits('submit', confirm)
  show.value = false
}
</script>

<style lang="scss">
.el-dialog.confirm-dialog  {
  --el-dialog-padding-primary: 0;
  --el-dialog-margin-top: 0;
  top: 50%;
  transform: translateY(-100%);
  .el-dialog__header {
    padding: 10px 14px;
    display: flex;
    justify-content: space-between;
  }
  .el-dialog__footer {
    display: flex;
    padding-bottom: 24px;
    justify-content: center;
    .el-button {
      width: 82px;
      height: 34px;
    }
  }
  .message {
    white-space: pre-wrap;
    word-break: break-all;
  }
}
</style>