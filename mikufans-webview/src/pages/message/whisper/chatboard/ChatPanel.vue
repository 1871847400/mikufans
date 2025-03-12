<template>
  <div class="chat-panel" v-if="targetId">
    <div class="toolbar">
      <i class="iconfont" @click="onImageSelect" title="图片">&#xe695;</i>
      <el-popover trigger="click" placement="bottom-start" width="310px">
        <emoji-selector :inputRef="richRef"/>
        <template #reference>
          <i class="iconfont" title="表情">&#xe646;</i>
        </template>
      </el-popover>
    </div>
    <rich-input 
      ref="richRef"
      class="h-[100px]"
      placeholder="请在这里输入聊天内容..."
      @submit="onSubmit"
    />
    <div class="bottom-bar" v-if="richRef">
      <div class="word-limit" :data-full="richRef.length>=512">{{richRef.length}}/512</div>
      <el-button class="w-20 h-7" type="primary" @click="onSubmit" :disabled="!richRef.text?.trim()">发送</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import EmojiSelector from '@/components/EmojiSelector/index.vue';
import { openFileSelect } from '@/utils/dialog'
import { uploadImage } from '@/apis/image';
import whisperApi from '@/apis/user/message/whisper';
const { targetId } = storeToRefs(useMsgStore())
const richRef = useTemplateRef('richRef')
async function onSubmit() {
  const text = richRef.value.text
  if (!text.trim()) {
    return //如果内容为空则什么也不干
  }
  await sendWhisper(text, 1)
  richRef.value.clear()
}
function onImageSelect() {
  openFileSelect({
    accept: 'image/*',
    async callback(file: File) {
      const rid = await uploadImage(file)
      sendWhisper(rid, 2)
    },
  })
}
function sendWhisper(message: string, msgType: 1|2) {
  return whisperApi.create({
    message,
    msgType,
    receiverId: targetId.value,
  })
}
</script>

<style scoped lang="scss">
.chat-panel {
  padding-left: 12px;
  border-top: 1px solid #88888873;
}
.toolbar {
  height: 40px;
  display: flex;
  align-items: center;
  user-select: none;
  box-sizing: border-box;
  .iconfont {
    font-size: 24px;
    margin-right: 8px;
    font-weight: bold;
    color: var(--grey2);
    cursor: pointer;
    user-select: none;
    transition: .3s color;
    &:hover {
      color: var(--blue1);
    }
  }
}
.bottom-bar {
  display: flex;
  align-items: center;
  flex-flow: row-reverse;
  height: 40px;
  padding-right: 12px;
}
.word-limit {
  font-size: 12px;
  color: var(--grey2);
  margin-right: 8px;
  order: 1;
  &[data-full=true] {
    color: red;
  }
}
</style>