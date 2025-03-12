<template>
  <div class="mt-4 flex gap-2">
    <user-avatar size="40px" :popover="false"/>
    <div class="flex-1">
      <div class="custom-input">
        <rich-input
          ref="richRef"
          autofocus
          v-model="shareReason" 
          :placeholder="userStore.isLogin?'请输入转发理由':'登录后才能转发动态'" 
          :disabled="!userStore.isLogin"
        />
      </div>
      <div class="flex items-center mt-2 gap-4">
        <el-popover trigger="click" placement="bottom-start" width="310px">
          <emoji-selector :inputRef="richRef"/>
          <template #reference>
            <i class="iconfont emoji" title="表情">&#xe646;</i>
          </template>
        </el-popover>
        <span class="ml-auto text-sm grey2">{{ shareReason.length }}/1000</span>
        <el-button type="primary" :loading="submitting" @click="share" :disabled="!shareReason">转发</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import userDynamicApi from '@/apis/user/dynamic'
const richRef = useTemplateRef('richRef')
const props = defineProps<{
  data: UserDynamic
}>()
const { emitEvent  } = useDynamicStore()
const userStore = useUserStore()
const shareReason = ref('')
if (props.data.shareId != '0') {
  shareReason.value = '@' + props.data.user.nickname + ':' + props.data.shareReason
}
onMounted(()=>{
  richRef.value.focus()
})
const submitting = ref(false)
async function share() {
  submitting.value = true
  try {
    const result = await userDynamicApi.createShare({
      shareId: props.data.id,
      shareReason: shareReason.value,
    })
    emitEvent('newDynamic', result)
    shareReason.value = ''
    message.success('转发成功')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.custom-input  {
  flex: 1;
  border: 1px solid var(--grey2);
  border-radius: 8px;
  padding: 8px;
  &:focus-within {
    border: 1px solid var(--blue0);
  }
  .rich-input {
    height: 60px;
  }
}
.iconfont.emoji {
  cursor: pointer;
  font-size: 24px;
  transition: all .2s;
  &:hover {
    color: var(--blue0);
  }
}
</style>