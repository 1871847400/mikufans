<template>
  <form class="user-sign" :title="user.sign" @submit.prevent="input.blur()">
    <input
      ref="input"
      :class="{'pointer-events-none':!isSelf}" 
      v-model="value" 
      type="text" 
      placeholder="个性签名" 
      maxlength="60"
      @blur="onSubmit"
    >
  </form>
</template>

<script setup lang="ts">
import userApi from '@/apis/user';
const { user, isSelf } = toRefs(useSpaceStore())
const userStore = useUserStore()
const value = ref(user.value.sign||'')
const input = ref<HTMLInputElement>()
//如果在其他地方修改了签名
watch(()=>userStore.sign, (sign)=>{
  if (isSelf.value) {
    value.value = sign
  }
})
async function onSubmit() {
  if (user.value.sign === value.value) {
    return
  }
  try {
    await userApi.update({
      id: userStore.id,
      sign: value.value
    })
    userStore.sign = value.value
    user.value.sign = value.value
    message.success('修改签名成功')
  } catch {
    value.value = user.value.sign
  }
}
</script>

<style scoped lang="scss">
.user-sign {
  input {
    font-size: 12px;
    width: 60em;
    padding: 4px;
    border-radius: 4px;
    background: none;
    transition: all .3s;
    color: rgba(255,255,255,.8);
    &::placeholder {
      color: rgba(255,255,255,.8);
    }
    &:focus {
      background: #eee;
      color: #333;
      &::placeholder {
        color: #666;
      }
    }
    &:hover:not(:focus) {
      background: rgba(255,255,255,.2);
      box-shadow: 0 0 0 1px rgba(255,255,255,.5);
    }
  }
}
</style>