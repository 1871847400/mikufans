<template>
  <div>
    <div class="flex items-center relative">
      <div class="text-lg mr-2">{{ isSelf ? '我' : 'TA' }}的代表视频</div>
      <i v-if="isSelf" class="iconfont icon-jia grey1 cursor-pointer" @click="appendDialog"></i>
      <span v-if="state.length==0" class="grey2 abs-center">暂时什么也没有</span>
    </div>
    <el-divider class="my-2" />
    <ul class="flex flex-col gap-4">
      <template v-for="{ id, reason, video } in state" :key="id">
        <li class="flex gap-4">
          <a class="relative h-fit" :href="video.uri" target="_blank">
            <miku-image class="w-[240px]" video :res-id="video.bannerId" />
            <span class="duration">{{ displayDuration(video.duration) }}</span>
          </a>
          <div class="text-xs flex-1">
            <div class="flex items-center">
              <a class="maxline-2 pb-2 text-sm w-fit" :href="video.uri" target="_blank">{{ video.title }}</a>
              <div class="ml-auto" v-if="isSelf">
                <span class="cursor-pointer" title="删除" @click="remove(id)">
                  <i class="iconfont icon-lajixiang"></i>
                </span>
              </div>
            </div>
            <div class="flex">
              <!-- <stats-icon icon="" :count="i." /> -->
            </div>
            <div class="grey2 min-h-[40px] mt-2">
              <rich-text html :content="video.intro" :rows="3" />
            </div>
            <div class="text-red-400 mt-1">
              <span>推荐理由：</span>
              <span class="break-all">{{ reason }}</span>
            </div>
          </div>
        </li>
      </template>
    </ul>
  </div>
  <el-divider v-if="state.length"/>
</template>

<script setup lang="ts">
import { openDialog } from '@/utils/dialog';
import RepresentDialog from './RepresentDialog.vue'
import representApi from '@/apis/video/represent';
import { displayDuration } from '@/utils/common';
const { userId, isSelf } = toRefs(useSpaceStore())
const { state, execute } = useAsyncState(()=>representApi.listByUserId(userId.value), [])
async function remove(id: string) {
  await message.confirm('你确定删除该推荐吗？不会删除原视频')
  await representApi.removeById(id)
  await execute()
  message.success('删除成功')
}
function appendDialog() {
  openDialog(RepresentDialog, {
    onSubmit() {
      execute()
    }
  })
}
</script>

<style scoped lang="scss">
.duration {
  position: absolute;
  right: 8px;
  bottom: 8px;
  color: #fff;
  border-radius: 6px;
  padding: 1px 4px;
  background-color: #0000008c;
  font-size: 12px;
  pointer-events: none;
}
</style>