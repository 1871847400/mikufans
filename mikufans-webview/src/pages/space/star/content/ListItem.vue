<template>
  <div>
    <a class="video-banner" :href="video?.uri" target="_blank">
      <miku-image class="w-full" video :res-id="video?.bannerId" />
      <span class="duration">{{ video ? displayDuration(video.duration) : '00:00' }}</span>
    </a>
    <div>
      <a class="video-title hoverable maxline-2" :href="video?.uri" target="_blank">{{ title }}</a>
      <div class="flex grey1 justify-between text-xs">
        <span>
          <span class="mr-1">收藏于</span>
          <span>{{ data.starDate }}</span>
        </span>
        <el-dropdown class="actions" v-if="isSelf" @command="executeCommand">
          <span class="iconfont text-xs" @click.prevent>&#xeb10;</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="0">取消收藏</el-dropdown-item>
              <el-dropdown-item command="1" v-if="!invalid">移动到</el-dropdown-item>
              <el-dropdown-item command="2" v-if="!invalid">复制到</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import FavDialog from '@/pages/video/left/favorite/FavDialog.vue';
import { displayDuration } from '@/utils/common';
import { openDialog } from '@/utils/dialog';
import videoStarApi from '@/apis/video/star';
import { useStore } from '../store';
const props = defineProps<{ data: VideoStar }>()
const emits = defineEmits(['delete'])
const video = toRef(()=>props.data.video)
const { isSelf } = storeToRefs(useSpaceStore())
const { userStars } = useStore()
//视频可能因为被删除而失效
const invalid = computed(()=>!video.value)
const title = computed(()=>invalid.value ? '该视频已失效' : props.data.highlighted || video.value.title)
//点击下拉框时
async function executeCommand(cmd: string) {
  if (cmd == '0') {
    await cancelVideoStar(props.data)
    emits('delete')
  } else {
    openDialog(FavDialog, {
      videoId: video.value.id,
      videoStar: props.data,
      title: cmd=='1'?'移动收藏到':'复制收藏到',
      async onSubmit() {
        if (cmd == '1') {
          await cancelVideoStar(props.data)
          emits('delete')
        }
      }
    })
  }
}
async function cancelVideoStar(videoStar: VideoStar) {
  await videoStarApi.save({
    videoId: videoStar.videoId,
    delList: [videoStar.starId]
  })
  const userStar = userStars.value.find(v=>v.id===videoStar.starId)
  if (userStar) {
    userStar.starCount--
  }
}
</script>

<style scoped lang="scss">
.video-item {
  cursor: pointer;
}
.video-title {
  font-size: 14px;
  line-height: 20px;
  height: 40px;
  margin: 4px 0;
}
.video-banner {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 0 2px rgba(0, 0, 0, .2);
  .duration {
    pointer-events: none;
    position: absolute;
    right: 4px;
    bottom: 4px;
    color: #fff;
    font-size: 12px;
    background-color: rgba(0, 0, 0, .5);
    border-radius: 6px;
    padding: 4px;
  }
}
</style>