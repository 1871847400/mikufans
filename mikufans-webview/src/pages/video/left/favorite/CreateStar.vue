<template>
  <!-- 新建收藏夹按钮 -->
  <form class="new-star-button" @submit.prevent="createUserStar" @click="change(true)">
    <template v-if="!editing">
      <span class="iconfont plus">&#xe63c;</span>
      <span>新建收藏夹</span>
    </template>
    <template v-else>
      <input 
        type="text" 
        v-model.trim="starName" 
        placeholder="请输入收藏夹名称" 
        v-focus 
        @blur="change(false,100)" 
        maxlength="16"
      >
      <button class="create-btn" type="submit">新建</button>
    </template>
  </form>
</template>

<script setup lang="ts">
import { isBlank } from '@/utils/common';
import userStarApi from '@/apis/user/star';
const emits = defineEmits<{
  submit: [star: UserStar]
}>()
// 是否处于编辑状态
const editing = ref(false)
// 收藏夹名称
const starName = ref('')
const submitting = ref(false)
//blur事件先于submit,必须延迟才能触发form的submit
function change(status, timeout=0) {
  setTimeout(() => {
    editing.value = status
  }, timeout);
}
async function createUserStar() {
  editing.value = false
  if (isBlank(starName.value) || submitting.value) return
  try {
    submitting.value = true
    const data = await userStarApi.create({
      starName: starName.value,
      visible: 0,
      intro: '',
    })
    emits('submit', data)
    starName.value = ''
    message.success('创建收藏夹成功！')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.new-star-button {
  box-sizing: border-box;
  margin: 0 auto;
  margin-top: 12px;
  width: 85%;
  height: 40px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  border: 2px solid #ccc;
  cursor: pointer;
  &:hover, &:focus-within{
    border-color: #79bbff;
  }
  input {
    text-indent: 8px;
    border: none;
    outline: none;
    width: 80%;
    height: 100%;
    font-size: 18px;
    &::placeholder {
      font-size: 13px;
    }
  }
  .create-btn {
    width: 20%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #d9f1f9;
    border: none;
    border-left: 2px solid #ccc;
    cursor: pointer;
    &:hover {
      color: #79bbff;
    }
  }
}
.plus {
  padding: 0 8px; 
  font-size: 24px;
  font-weight: 600;
  color: #999;
}
</style>