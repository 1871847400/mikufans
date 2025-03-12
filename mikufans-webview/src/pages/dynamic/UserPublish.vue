<template>
  <form class="bg0 rounded-lg p-4 box-border h-fit" @submit.prevent="submit">
    <div class="flex gap-4">
      <input v-model.trim="data.title" class="title" maxlength="20" type="text" placeholder="标题(选填)">
      <span class="text-sm grey2">{{ data.title.length }}/20</span>
    </div>
    <el-divider class="mt-2 mb-1"/>
    <rich-input
      v-model="data.content"
      ref="richRef"
      class="h-[40px] max-h-[120px]"
      placeholder="有什么想和大家分享的?"
    />
    <image-select-list v-model="files"/>
    <div class="flex items-center gap-4 mt-4" v-if="richRef" @mousedown.prevent>
      <i class="iconfont popover-icon" @click="open()" title="图片">&#xe695;</i>
      <el-popover trigger="click" placement="bottom-start" width="310px">
        <emoji-selector :inputRef="richRef"/>
        <template #reference>
          <i class="iconfont popover-icon" title="表情">&#xe646;</i>
        </template>
      </el-popover>
      <span class="ml-auto"></span>
      <span class="text-sm grey2">{{ richRef.length }}/2048</span>
      <el-button type="primary" native-type="submit" :loading="submitting" :disabled="!data.content">发布</el-button>
    </div>
  </form>
</template>

<script setup lang="ts">
import RichInput from '@/components/RichInput/index.vue';
import userPublishApi from '@/apis/user/publish'
import { resetReactive } from '@/hooks/resetReactive';
import { useImageSelect } from '@/hooks/useImageSelect';
import userDynamicApi from '@/apis/user/dynamic';
import { emitGlobalEvent } from '@/hooks/mitt';
const { emitEvent } = useDynamicStore()
const richRef = ref<InstanceType<typeof RichInput>>()
const [data, reset] = resetReactive<UserPublishDto>({
  title: '',
  content: '',
  imgIds: [],
  dynamic: {
    publishFlag: 0,
    publishTime: '',
    visible: 1,
    commentArea: {
      commentFlag: 'NORMAL',
      userLevel: 0,
    }
  }
})
const { files, open } = useImageSelect({
  multiple: true,
  max: 20,
  autoUpload: true,
})

const submitting = ref(false)
async function submit() {
  try {
    submitting.value = true
    if (files.value.some(a=>a.uploadStatus==='uploading')) {
      await message.confirm('还有图片未上传完成,确定提交吗？')
    }
    data.imgIds = files.value
      .filter(a=>a.uploadStatus==='success')
      .map(a=>a.resId)
    const result = await userPublishApi.create(toRaw(data))
    const dynamic = await userDynamicApi.getById(result.id)
    message.success('发布成功')
    reset()
    files.value = []
    if (dynamic) {
      emitEvent('newDynamic', dynamic)
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
input.title {
  width: 100%;
  font-weight: 550;
  font-size: 16px;
  background: none;
  &::placeholder {
    font-weight: 500;
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
</style>