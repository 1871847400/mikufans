<template>
  <div class="chat-board">
    <div class="title">{{ target?.nickname ?? '' }}</div>
    <whispers-display />
    <chat-panel />
  </div>
</template>

<script setup lang="ts">
import ChatPanel from './ChatPanel.vue';
import WhispersDisplay from './WhispersDisplay.vue';
import userApi from '@/apis/user';
const { targetId } = toRefs(useMsgStore())
//聊天对象(可能不存在)
const target = shallowRef<User>(null)
//使用promise防止旧请求覆盖新数据
let promise = Promise.resolve()
//监听当用户更改地址的对象id后，重新加载对象的资料
watchImmediate(targetId, id=>{
  promise = promise.finally(()=>callback(id))
})
async function callback(id: string | null) {
  if (id && id !== '0') {
    target.value = await userApi.fetch(id)
  } else {
    target.value = null
  }
}
</script>

<style scoped lang="scss">
.chat-board {
  flex: 1 0 0;
  display: flex;
  flex-direction: column;
  background-color: hsla(0, 0%, 100%, .9);
  html.dark & {
    background-color: rgb(48, 49, 51, .9);
  }
}
.title {
  line-height: 36px;
  background-color: var(--bg0);
  border-bottom: 1px solid var(--bg1);
  box-sizing: border-box;
  display: flex;
  font-size: 18px;
  justify-content: center;
  align-items: center;
}
</style>