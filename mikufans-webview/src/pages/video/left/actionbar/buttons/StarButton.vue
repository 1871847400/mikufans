<template>
  <div 
    class="action-item" 
    title="收藏"
    :class="{ active: video.userStar }"
    @click="open"
  >
    <circle-progress v-model="triple.progress">
      <div ref="iconRef">
        <i class="iconfont icon-shoucang_shixin text-[28px]"></i>
      </div>
    </circle-progress>
    <action-number :number="video.stars"/>
  </div>
</template>

<script setup lang="ts">
import { openDialog } from '@/utils/dialog';
import ActionNumber from '../ActionNumber.vue';
import FavDialog from '../../favorite/FavDialog.vue';
import { splash } from '@/utils/animation';
import videoStarApi from '@/apis/video/star';
const userStore = useUserStore()
const { video } = toRefs(useVideoStore())
const { triple } = useVideoStore()
//防止无限打开
let isOpen = false
const { e } = useMagicKeys()
watch(e, (pressed)=>{
  if (pressed && !isOpen && document.activeElement.tagName === 'BODY') {
    open()
  }
})
async function open() {
  if (isOpen) {
    return
  }
  if (!userStore.isLogin) {
    return message.warning('请先登录')
  }
  isOpen = true
  try {
    await openDialog(FavDialog, {
      videoId: video.value.id,
      onSubmit: updateStarInfo
    })
  } finally {
    isOpen = false
  }
}
function updateStarInfo(newStatus: boolean) {
  const old = video.value.userStar
  if (old && !newStatus) {
    video.value.stars--
  } else if (!old && newStatus) {
    video.value.stars++
  }
  video.value.userStar = newStatus
}
const iconRef = ref<HTMLDivElement>(null)
function handle() {
  splash({
    container: iconRef.value,
    offset: 0,
    color: triple.color_dark,
    size: 12,
  })
  splash({
    container: iconRef.value,
    offset: 22,
    color: triple.color_light,
    size: 8,
    delay: 0.1,
  })
  videoStarApi.save({
    videoId: video.value.id,
    addList: ['0']
  }).then(updateStarInfo)
}
onBeforeUnmount(triple.trigger.on(handle).off)
</script>