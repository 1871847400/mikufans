<template>
  <div class="reply-item">
    <user-avatar :user="comment.user" size="50px"/>
    <div class="flex-1">
      <div class="mb-2">
        <router-link 
          class="font-bold mr-1 hover:text-[#00aeec]"
          :title="comment.user.nickname" 
          :to="user.uri" 
          target="_blank"
        >
          {{ comment.user.nickname }}
        </router-link>
        <span>在{{ source.business }}</span>
        <router-link v-if="source" class="mx-1" :to="source.uri" target="_blank" :title="'查看'+source.business">
          <em class="">{{ source.message?.substring(0, 20) }}...</em>
        </router-link>
        <span v-else class="grey2 italic mr-2">(已失效)</span>
        <span>{{ at ? '@了您' : '回复了您' }}</span>
      </div>
      <a :href="uri" target="_blank" title="查看评论">
        <div class="max-w-[80%] bg1 p-2 rounded">
          <rich-text :rows="2" html :content="convertComment(comment)"/>
        </div>
      </a>
      <div class="text-sm grey2 mt-2 flex items-center">
        <span class="mr-8">{{ publishTime }}</span>
        <div class="min-w-16 mr-1">
          <i class="iconfont" :active="comment.likeStatus.likeVal==1" @click="like" title="点赞">&#xec7f; {{ displayNumber(comment.likeStatus.likes) }}</i>
        </div>
        <i class="iconfont" :active="comment.likeStatus.likeVal==2" @click="dislike" title="点踩">&#xe610; {{ displayNumber(comment.likeStatus.dislikes) }}</i>
      </div>
      <el-divider class="my-2"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import { convertComment } from '@/components/CommentArea/utils';
import { useLike } from '@/hooks/useLike';
import { displayNumber } from '@/utils/common';
const props = defineProps<{ msg: UserCommentMsg, at?: boolean }>()
const { 
  user,
  comment,
  source,
  publishTime,
  uri
} = toRefs(props.msg)
const { like, dislike } = useLike(comment.value.likeStatus)
</script>

<style scoped lang="scss">
.reply-item {
  display: flex;
  column-gap: 12px;
  padding: 4px 12px;
  background-color: var(--bg0);
  :deep(.at-user) {
    color: var(--blue0);
  }
  i.iconfont {
    cursor: pointer;
    &[active='true'] {
      color: var(--blue0);
    }
  }
  &:first-of-type {
    border-radius: 8px 8px 0 0; //防止动画时锐角
    padding-top: 16px;
  }
  &:last-of-type {
    border-radius: 0 0 8px 8px;
    padding-bottom: 16px;
  }
}
</style>