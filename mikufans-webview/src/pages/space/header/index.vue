<template>
  <div class="header" :style="{backgroundImage:`url(${bgUrl})`}">
    <background-select v-if="isSelf"/>
    <div class="user">
      <div v-loading="submitting" :class="{ 'pointer-events-none': submitting }" class="user-avatar-box" @click.prevent="onImageSelect">
        <user-avatar :user="user" :data-self="isSelf" :popover="false" size="100%"/>
      </div>
      <div class="user-profile">
        <div class="flex gap-2 items-center">
          <div class="user-name" :title="'昵称：'+user.nickname">{{ user.nickname || '未知昵称' }}</div>
          <user-gender :gender="user.gender" />
          <svg-icon :name="'level_'+user.level" :size="32"/>
        </div>
        <user-sign/>
      </div>
    </div>
    <div class="action-list" v-if="!userStore.isSelf(user.id)">
      <user-follow-button :user-id="user.id" :status="user.follow"/>
      <el-button class="w-[90px]" plain @click="openUrl">发消息</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { uploadImage } from '@/apis/image';
import BackgroundSelect from './BackgroundSelect.vue';
import { baseURL } from '@/apis/service'
import UserSign from './UserSign.vue';
import { openDialog, openFileSelect } from '@/utils/dialog';
import ImageCropDialog from '@/components/ImageCropDialog/index.vue'
import userApi from '@/apis/user';
import { getImageSize } from '@/utils/image/img_size';
const { user, isSelf } = toRefs(useSpaceStore())
const userStore = useUserStore()
const submitting = ref(false)
async function onImageSelect() {
  if (!isSelf.value) {
    return
  }
  openFileSelect({
    accept: 'image/*',
    async callback(file) {
      const { width, height } = await getImageSize(file)
      if (width < 64 || height < 64) {
        return message.warning(`图片尺寸不得小于${width}x${height}`)
      }
      openDialog(ImageCropDialog, {
        src: file,
        ratio: 1,
        round: true,
        async onSubmit(data) {
          submitting.value = true
          try {
            const imgId = await uploadImage(data)
            await userApi.update({
              id: userStore.id,
              avatarId: imgId
            })
            userStore.avatarId = imgId
            if (isSelf.value) {
              user.value.avatarId = imgId
            }
            message.success('更换头像成功')
          } finally {
            submitting.value = false
          }
        }
      })
    },
  })
}
const bgUrl = computed(()=>{
  const bg = isSelf.value ? userStore.background : user.value.background
  return baseURL + userStore.backgrounds[bg]
})
function openUrl() {
  if (userStore.isLogin) {
    window.open('/msg/whisper#' + user.value.id, '_blank')
  } else {
    userStore.login()
  }
}
</script>

<style scoped lang="scss">
.header {
  height: 180px;
  box-sizing: border-box;
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
  transition: background .3s ease;
  background-color: #ccc;
  position: relative;
  html.dark & {
    filter: brightness(.8);
  }
  /* 让用户信息下显示一层阴影,以免字看不清 */
  &::before {
    content: '';
    background: linear-gradient(0, #000000a4, #00000059);
    filter: blur(25px);
    position: absolute;
    inset: 0;
    top: 75%;
  }
  .user {
    position: absolute;
    left: 16px;
    bottom: 12px;
    display: flex;
    align-items: center;
    width: 50%;
  }
  .user-avatar-box {
    width: 64px;
    height: 64px;
    line-height: 64px;
    border-radius: 50%;
    margin-right: 16px;
    flex-shrink: 0;
    overflow: hidden;
    :deep(.user-avatar) {
      outline: 3px solid #fff;
      position: relative;
      text-align: center;
      font-size: 12px;
      &[data-self=false] {
        pointer-events: none;
      }
      &[data-self=true]:hover {
        :deep(img) {
          opacity: 0.7;
        }
        &::after {
          content: '更换头像';
          color: #fff;
          background-color: rgba(0,0,0,.3);
          inset: 0;
          position: absolute;
          pointer-events: none;
        }
      }
    }
  }
  .user-profile {
    flex: 1;
    width: 100%;
    .user-name {
      font-size: 18px;
      line-height: 26px;
      color: #fff;
      font-weight: 550;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
  .action-list {
    position: absolute;
    right: 20px;
    bottom: 12px;
    display: flex;
    gap: 12px;
  }
}
</style>