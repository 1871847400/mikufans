<template>
  <el-dropdown placement="bottom" trigger="hover" @command="handlers[$event]()">
    <span class="text-lg font-bold">
      <i class="iconfont icon-24gf-ellipsisVertical"></i>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="onMobile">用手机看</el-dropdown-item>
        <el-dropdown-item v-if="!isSelf(video.userId)" command="onReport">举报</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import ReportDialog from '@/dialogs/ReportDialog.vue';
import { openDialog } from '@/utils/dialog';
const { video } = toRefs(useVideoStore())
const { isSelf } = useUserStore()
const handlers = {
  onReport() {
    openDialog(ReportDialog, {
      targetId: video.value?.id,
      reportType: 'VIDEO'
    })
  },
  onMobile() {
    message.warning('敬请期待')
  }
}
</script>

<style scoped lang="scss">
</style>