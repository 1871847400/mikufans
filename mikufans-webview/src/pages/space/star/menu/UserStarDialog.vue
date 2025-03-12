<template>
  <el-dialog v-model="dialog" :title="title" :lock-scroll="false" center width="450px" :close-on-click-modal="false">
    <el-form v-if="form" label-position="top" label-suffix=":" @submit.native.prevent="submit">
      <el-form-item label="收藏夹封面">
        <div class="cursor-pointer" @click="upload">
          <miku-image :res-id="form.coverId" video class="w-[150px]" />
        </div>
      </el-form-item>
      <el-form-item label="收藏夹名称" required>
        <el-input v-model.trim="form.starName" :disabled="userStar?.defFlag==1" maxlength="16" show-word-limit autofocus placeholder="请输入名称" />
      </el-form-item>
      <el-form-item label="简介">
        <el-input v-model="form.intro" type="textarea" resize="none" maxlength="100" show-word-limit placeholder="请描述下收藏夹" />
      </el-form-item>
      <el-checkbox v-model="form.visible" :true-value="1" :false-value="0">公开收藏夹</el-checkbox>
      <br><br>
      <el-button type="primary" class="w-full" native-type="submit">保存</el-button>
    </el-form>
  </el-dialog>
</template>

<script setup lang="ts">
import { uploadImage } from '@/apis/image';
import userStarApi from '@/apis/user/star'
import { openDialog, openFileSelect } from '@/utils/dialog';
import ImageCropDialog from '@/components/ImageCropDialog/index.vue';
const props = defineProps<{
  userStar?: UserStar //如果不为空代表修改,否则创建
}>()
const title = props.userStar ? '编辑收藏夹' : '新增收藏夹'
const emits = defineEmits<{ submit: [data: UserStar] }>()
const dialog = ref(true)
const form = ref<UserStarDto>({
  starName: '',
  visible: 1,
  intro: '',
  ...props.userStar
})
function upload() {
  openFileSelect({
    accept: 'image/*',
    callback(file) {
      openDialog(ImageCropDialog, {
        ratio: 16/9,
        src: file,
        async onSubmit(data) {
          const resId = await uploadImage(data)
          form.value.coverId = resId
        }
      })
    },
  })
}
async function submit() {
  if (form.value.id) {
    await userStarApi.updateById(form.value)
    emits('submit', {
      ...props.userStar,
      ...form.value
    })
    message.success('更新收藏夹成功')
  } else {
    const data = await userStarApi.create(form.value)
    emits('submit', data)
    message.success('新增收藏夹成功')
  }
  dialog.value = false
}
</script>