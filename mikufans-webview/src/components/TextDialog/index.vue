<template>
  <!-- 外层覆盖整个窗口 -->
  <div class="dialog-overlay">
    <!-- 弹窗内容区 -->
    <div class="dialog-wrap">
      <div class="dialog-header">
        <span>{{ title }}</span>
        <span class="iconfont close" @click.stop="close">&#xe647;</span>
      </div>
      <div class="dialog-body">
        <div>{{ content }}</div>
        <div class="buttons">
          <el-button class="confirm" type="primary" @click.stop="confirm">确定</el-button>
          <el-button class="confirm" @click.stop="close">取消</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
const props = defineProps({
  title: {
    type: String,
    default: '提示'
  }, 
  content: {
    type: String,
    default: '',
  },
})
const emits = defineEmits<{
  closed: [action?: 'confirm']
}>()
function confirm() {
  emits('closed', 'confirm')
}
function close() {
  emits('closed')
}
</script>

<style scoped lang="scss">
.dialog-overlay {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  z-index: 10000;
  background-color: rgba(0,0,0,.5);
}
.dialog-wrap {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%,-50%);
  background-color: var(--bg0);
  min-width: 380px;
  min-height: 190px;
  box-sizing: border-box;
  box-shadow: 0px 0px 12px rgba(0, 0, 0, .12);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}
.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  font-size: 16px;
  border-bottom: 1px solid #e5e9ef;
  .close {
    cursor: pointer;
    user-select: none;
    font-weight: 600;
  }
}
.dialog-body {
  flex: 1;
  padding: 18px;
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  .buttons {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    .el-button {
      width: 100px;
      height: 36px;
    }
  }
}
</style>