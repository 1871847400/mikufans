<template>
  <div class="whisper-section flex-1">
    <dialog-list/>
    <chat-board/>
  </div>
</template>

<script setup lang="ts">
import DialogList from './DialogList.vue';
import ChatBoard from './chatboard/index.vue';
const { targetId } = toRefs(useMsgStore())
const userStore = useUserStore()
watchImmediate(targetId, ()=>{
  if (userStore.isSelf(targetId)) {
    targetId.value = null
  }
})
</script>

<style scoped lang="scss">
.whisper-section {
  --border-color: rgba(0,0,0,.1);
  display: flex;
  overflow: hidden; //此页面不滚动
  border-radius: 4px;
}
</style>