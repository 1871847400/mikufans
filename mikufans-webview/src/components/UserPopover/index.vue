<template>
  <el-popover
    class="user-popover"
    placement="bottom-start"
    trigger="hover"
    :width="320"
    @before-enter="beforeOpen"
    :disabled="!popover"
  >
    <template v-if="popover">
      <div v-if="userData.background" class="user-bg" :style="{backgroundImage}"></div>
      <div class="flex gap-3 p-4 pt-3">
        <router-link class="h-fit" :to="userData.uri" target="_blank" title="进入TA的空间">
          <miku-image :res-id="userData.avatarId" avatar :style="{width: size, height: size}"/>
        </router-link>
        <div>
          <div class="flex items-center gap-2 mb-2">
            <a class="font-bold" :href="userData.uri" target="_blank" title="进入TA的空间">{{ userData.nickname }}</a>
            <user-gender :gender="userData.gender" :background="false" />
            <svg-icon :name="'level_'+userData.level" :size="32"/>
          </div>
          <div v-if="isLoading">加载用户数据中...</div>
          <template v-else>
            <div class="flex items-center gap-4 text-xs">
              <div v-for="i in numbers">
                <span class="mr-1">{{ displayNumber(i[0] as number) }}</span>
                <span class="grey2">{{ i[1] }}</span>
              </div>
            </div>
            <div class="text-xs mt-2 grey2 maxline-3">{{ userData.sign }}</div>
            <div v-if="!isSelf(userData.id)" class="mt-2 flex items-center gap-2">
              <user-follow-button :user-id="userData.id" :status="userData.follow"/>
              <el-button class="w-[90px]" plain @click="sendMsg">发消息</el-button>
            </div>
          </template>
        </div>
      </div>
    </template>
    <template #reference>
      <slot></slot>
    </template>
  </el-popover>
</template>

<script setup lang="ts">
import { baseURL } from '@/apis/service';
import userApi from '@/apis/user';
import { displayNumber } from '@/utils/common';
import { PropType } from 'vue';
const props = defineProps({
  user: {
    type: Object as PropType<User | BaseEntity['user']>,
    required: true,
  },
  popover: {
    type: Boolean,
    default: true,
  },
  //头像图片的大小
  size: {
    type: String,
    default: '60px'
  }
})
const { isSelf, backgrounds } = toRefs(useUserStore())
const userData = reactive<User>(props.user as User)
const { execute, isLoading } = useAsyncState(()=>userApi.fetch(props.user.id), null, {
  immediate: false,
  onSuccess(data) {
    Object.assign(userData, data)
  },
})
function beforeOpen() {
  if (!isLoading.value) {
    execute()
  }
}
function sendMsg() {
  window.open('/msg/whisper#' + userData.id)
}
const numbers = computed(()=>([
  [userData.follows, '关注'],
  [userData.fans, '粉丝'],
  [userData.likes, '获赞'],
]))
const backgroundImage = computed(()=>`url(${baseURL+backgrounds.value[userData.background]})`)
</script>

<style scoped lang="scss">
/* 只显示了背景图中间的一部分 */
.user-bg {
  border-radius: 6px 6px 0 0;
  width: 100%;
  height: auto;
  aspect-ratio: 4.3; //并非图片真实比例
  background-size: cover;
  background-position: 50% 50%;
  background-repeat: no-repeat;
  html.dark & {
    filter: brightness(.8);
  }
}
</style>