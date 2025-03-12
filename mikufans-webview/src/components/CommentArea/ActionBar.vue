<template>
  <div class="comment-action-bar">
    <span class="mr-6">{{ comment.publishTime }}</span>
    <div class="min-w-14 mr-1 select-none">
      <i class="iconfont" :active="likeStatus.likeVal==1" @click="like" title="点赞">&#xec7f; {{ displayNumber(likeStatus.likes) }}</i>
    </div>
    <div class="min-w-14 mr-1 select-none">
      <i class="iconfont" :active="likeStatus.likeVal==2" @click="dislike" title="点踩">&#xe610; {{ displayNumber(likeStatus.dislikes) }}</i>
    </div>
    <el-button link @mousedown.stop @click="answer">{{answering?.id===comment.id?'取消回复':'回复'}}</el-button>
    <el-dropdown class="action ml-auto" placement="bottom" trigger="hover" @command="handlers[$event]()">
      <span>
        <i class="iconfont icon-24gf-ellipsisVertical"></i>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item v-if="userStore.isSelf(area.userId)&&!comment.isChild" command="onTop">{{ comment.top ? '取消置顶' : '设为置顶' }}</el-dropdown-item>
          <el-dropdown-item v-if="userStore.isSelf(area.userId)&&area.commentFlag==='CHOICE'" command="onSelect">{{ comment.selected ? '取消精选' : '设为精选' }}</el-dropdown-item>
          <el-dropdown-item v-if="userStore.isSelf(comment.userId)" command="onRemove">删除</el-dropdown-item>
          <el-dropdown-item v-else command="onReport">举报</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import commentApi from '@/apis/user/comment';
import { useStore } from './store';
import { displayNumber } from '@/utils/common';
import ReportDialog from '@/dialogs/ReportDialog.vue';
import { openDialog } from '@/utils/dialog';
import { useLike } from '@/hooks/useLike';
const props = defineProps<{ comment: UserComment }>()
const { likeStatus } = toRefs(props.comment)
const { answering, removeList, reset, area } = useStore()
const userStore = useUserStore()
const { like, dislike } = useLike(likeStatus)
function answer() {
  if (answering.value?.id === props.comment.id) {
    answering.value = null
  } else {
    answering.value = props.comment
  }
}
const handlers = {
  async onTop() {
    if (props.comment.top) {
      await commentApi.setTop(props.comment.id, false)
      message.success('取消置顶')
    } else {
      await commentApi.setTop(props.comment.id, true)
      message.success('置顶成功')
    }
    reset()
  },
  async onSelect() {
    if (props.comment.childCount) {
      await commentApi.setSelected(props.comment.id, false)
      message.success('取消精选')
    } else {
      await commentApi.setSelected(props.comment.id, true)
      message.success('已设置精选')
    }
    reset()
  },
  async onRemove() {
    await message.confirm('确定删除该评论吗?')
    const id = props.comment.id
    await commentApi.removeById(id)
    removeList.value.push(props.comment)
    if (answering.value?.id === id || answering.value?.pid === id) {
      answering.value = null
    }
    message.success('删除评论成功!')
  },
  onReport() {
    openDialog(ReportDialog, {
      reportType: 'COMMENT',
      targetId: props.comment.id,
    })
  }
}
</script>

<style scoped lang="scss">
.comment-action-bar {
  width: 100%;
  font-size: 14px;
  color: var(--grey2);
  display: flex;
  align-items: center;
  .iconfont {
    cursor: pointer;
    &[active=true], &:hover {
      color: var(--blue0);
    }
  }
}
.comment-item:hover .action {
  display: block;
}
</style>