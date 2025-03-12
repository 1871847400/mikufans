<template>
  <div class="comment-send">
    <user-avatar class="mr-3 pointer-events-none" size="60px"/>
    <div class="comment-send-input" :class="{ focus }" @focusin="focus=true">
      <div class="relative text-sm">
        <rich-input
          ref="richRef"
          @submit="sendComment"
          @node-remove="onNodeRemove"
          :disabled="disabled"
        />
        <div v-if="richRef?.text===''" class="grey2 absolute left-0 top-0" :class="{'pointer-events-none':isLogin}">
          <template v-if="!isLogin">
            <span class="app-text-btn" @click="login()">登录</span>后才能发送评论哦！
          </template>
          <template v-else-if="area.commentFlag==='DISABLED'">
            <span class="text-red-400">当前评论区已关闭！</span>
          </template>
          <template v-else-if="comment">
            <span class="mr-1">正在回复:</span>
            <em>{{ answering.user.nickname }}</em>
          </template>
          <template v-else>请在这里输入评论的内容...</template>
        </div>
      </div>
      <image-select-list v-model="files"/>
      <!-- mousedown.prevent防止脱离input的焦点 -->
      <div class="flex items-center gap-3" @mousedown.prevent :class="{'pointer-events-none': disabled}">
        <i class="iconfont popover-icon" @click="open()" title="图片">&#xe695;</i>
        <el-popover trigger="click" placement="bottom-start" width="310px">
          <emoji-selector :inputRef="richRef"/>
          <template #reference>
            <i class="iconfont popover-icon" title="表情">&#xe646;</i>
          </template>
        </el-popover>
        <el-popover trigger="click" placement="bottom-start" width="240px">
          <at-user-list v-if="isLogin" @select="insertAtUser"/>
          <template #reference>
            <span class="popover-icon" title="@已关注的用户">@</span>
          </template>
        </el-popover>
        <div class="word-limit" :style="{color:wordFull?'red':''}">{{richRef?.length+'/'+maxlength}}</div>
        <el-button 
          class="submit" 
          type="primary" 
          @click="sendComment" 
          title="Enter发送 Shift Enter换行" 
          :loading="submitting" 
          :disabled="!canSendComment">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="tsx">
import commentApi from '@/apis/user/comment';
import { hasClassParent } from '@/utils/css';
import { useStore } from './store';
import { useImageSelect } from '@/hooks/useImageSelect';
import { SimpleUser } from '../AtUserList/index.vue';
const props = defineProps<{ comment?: UserComment }>()
const { isLogin } = storeToRefs(useUserStore())
const { login } = useUserStore()
const { sendList, answering, area } = useStore()
const richRef = useTemplateRef('richRef')
//评论内容上限
const maxlength = 512
const focus = ref(false)
//当鼠标点击整个输入组件外的地方才释放input焦点
useEventListener('mousedown', e=>{
  if (!hasClassParent(e.target, 'comment-send-input')) {
    focus.value = false
  }
})
//图片列表
const { files, open } = useImageSelect({
  multiple: true,
  max: 5,
  autoUpload: true,
})
//at的用户列表 昵称:id
const atUsers = reactive<Record<string, string>>({})
//是否正在提交评论中
const submitting = ref(false)
onMounted(()=>{
  //如果是在子评论区则自动获取焦点
  if (props.comment) {
    richRef.value.focus()
  }
})
watch(answering, (comment)=>{
  if (comment) {
    const pid = props.comment?.id 
    if (comment.id === pid || comment.pid === pid) {
      richRef.value.focus()
    }
  }
})
const disabled = computed(()=>{
  return !isLogin.value || submitting.value || area.commentFlag==='DISABLED'
})
//是否可以发送评论
const canSendComment = computed(()=>{
  return isLogin.value && richRef.value?.text
})
//发送评论
async function sendComment() {
  submitting.value = true
  const content = richRef.value.text
  try {
    if (files.value.some(a=>a.uploadStatus==='uploading')) {
      await message.confirm('还有图片未上传完成,确定提交吗？')
    }
    const imageIds = files.value
      .filter(a=>a.uploadStatus==='success')
      .map(a=>a.resId)
    const data = await commentApi.create({
      areaId: area.id,
      pid: props.comment?.id,
      replyId: answering.value?.id,
      imageIds,
      content,
      atUsers,
    })
    //清空选择的图片
    files.value = []
    sendList.value.unshift(data)
    //评论成功后再清空内容
    richRef.value?.clear();
    richRef.value?.blur()
    message.success('发送评论成功！')
  } finally {
    submitting.value = false
  }
}
//评论是否达到输入限制
const wordFull = computed(()=>{
  return richRef.value && richRef.value.length>=maxlength
})
//插入@用户
function insertAtUser(user: SimpleUser) {
  const span = document.createElement('span')
  span.classList.add('atuser')
  span.dataset['nickname'] = user.nickname
  span.dataset['userid'] = user.id
  span.innerHTML = '@' + user.nickname
  span.style.color = '#409eff'
  span.contentEditable = 'false'
  richRef.value.insertNode(span)
  atUsers[user.nickname] = user.id
}
//当删除@用户时
function onNodeRemove(node: Node) {
  if (node instanceof HTMLSpanElement) {
    if (node.classList.contains('atuser')) {
      delete atUsers[node.dataset.nickname]
    }
  }
}
</script>

<style scoped lang="scss">
.comment-send {
  margin-top: 24px;
  margin-bottom: 24px;
  display: flex;
  position: relative;
}
.comment-send-input {
  flex: 1;
  box-sizing: border-box;
  border: 1px solid rgb(230, 230, 230);
  padding: 8px;
  /* padding-right: 0; */
  border-radius: 4px;
  .rich-input {
    height: 40px;
  }
  &.focus {
    border-color: var(--blue0);
  }
  &.focus .rich-input {
    height: 80px;
  }
}
.popover-icon {
  font-style: normal;
  font-size: 24px;
  color: #999;
  cursor: pointer;
  user-select: none;
  &:hover {
    color: var(--blue0);
  }
}
.word-limit {
  font-size: 12px;
  color: #999;
  flex: 1;
  text-align: right;
}
.submit {
  width: 70px; 
  height: 32px;
  font-size: 16px;
  font-weight: 550;
}
</style>