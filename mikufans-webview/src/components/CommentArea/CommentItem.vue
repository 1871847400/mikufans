<template>
  <div class="comment-item" ref="itemRef">
    <!-- 评论作者的头像 -->
    <user-avatar :user="comment.user" :size="comment.isChild ? '36px' : '48px'" class="mr-3"/>
    <div class="details" :child="comment.isChild">
      <!-- 显示评论作者的信息 -->
      <comment-header :comment="comment" />
      <div class="content">
        <!-- 回复用户昵称 -->
        <template v-if="comment.isChild&&comment.replyUserId!=parent.userId">
          <a class="blue1 mr-2" :href="'/space/'+comment.replyUserId" target="_blank">回复 {{ comment.replyUser.nickname }}:</a>
        </template>
        <!-- 评论内容 -->
        <rich-text :rows="4" :content="convertComment(comment)" :html="false" />
        <!-- 评论的图片列表 -->
        <image-list :img-ids="comment.imageIds" :small="comment.isChild"/>
      </div>
      <!-- 评论按钮栏 -->
      <action-bar :comment="comment"/>
      <!-- 子评论列表 -->
      <div class="mt-3" v-if="!comment.isChild" v-loading="loading">
        <template v-for="i in children" :key="i.id">
          <comment-item :parent="comment" :comment="i"/>
        </template>
      </div>
      <!-- 切换子评论页码 -->
      <el-pagination 
        v-if="page"
        hide-on-single-page
        layout="prev, pager, next"
        :total="page.total"
        :page-size="page.size"
        :current-page="page.current"
        @current-change="run"
      />
      <!-- 查看更多评论按钮 -->
      <el-button link v-if="comment.childCount>2&&!expand" @click="run(1)">共{{ comment.childCount }}条回复,点击查看</el-button>
      <!-- 发送评论框 -->
      <comment-send v-if="showInput" :comment="comment"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import CommentSend from './CommentSend.vue';
import commentApi from '@/apis/user/comment';
import ActionBar from './ActionBar.vue';
import { isString, uniqBy } from 'lodash';
import { usePage } from '@/hooks/usePage';
import { useStore } from './store';
import ImageList from './ImageList.vue';
import { convertComment, HASH_PREFIX } from './utils';
import { useRouteHash } from '@vueuse/router';
import CommentHeader from './CommentHeader.vue';
const props = defineProps<{
  parent?: UserComment
  comment: UserComment
}>()
const { answering, sendList, removeList, useHash } = useStore()
const hash = useRouteHash('')
const itemRef = ref<HTMLDivElement>()
//是否展开并显示子评论
const expand = ref(false)
//是否显示评论输入框
const showInput = computed(()=>{
  const id = props.comment.id
  return !props.comment.isChild && (answering.value?.id === id || answering.value?.pid === id)
})
const { page, loading, run } = usePage(search, {
  onSuccess() {
    //设置为已展开子评论
    expand.value = true
    //删除刚发送的子评论
    sendList.value = sendList.value.filter(a=>a.pid!==props.comment.id)
  }
})
//搜索子评论
function search(pageNum: number) {
  return commentApi.search({
    areaId: props.comment.areaId,
    replyId: props.comment.id,
    pageNum,
    pageSize: 5,
  })
}
const children = computed(()=>{
  const result = []
  if (expand.value) {
    result.push(...page.value.records)
  } else if (props.comment.hots) {
    result.push(...props.comment.hots)
  }
  result.push(...sendList.value.filter(a=>a.pid===props.comment.id).toReversed())
  return uniqBy(result, 'id')
    .filter(a=>!removeList.value.find(b=>a.id===b.id))
})
onMounted(()=>{
  if (useHash && hash.value?.startsWith(HASH_PREFIX)) {
    const id = hash.value.replace(HASH_PREFIX, '')
    if (id === props.comment.id) {
      itemRef.value.scrollIntoView({
        behavior: 'smooth',
        inline: 'start',
        block: 'center',
      })
      itemRef.value.setAttribute('highlight', 'true')
      hash.value = ''
    }
  }
})
</script>

<style scoped lang="scss">
.comment-item {
  display: flex;
  margin-bottom: 32px;
  :deep(.at-user) {
    color: #2ab9ed;
  }
  &[highlight='true'] {
    animation: highlight 0.5s alternate 3;
  }
  @keyframes highlight {
    0% {
      background-color: transparent;
    }
    100% {
      background-color: #dff6fd;
    }
  }
}
.details {
  flex: 1 0 0;
  //评论和评论之间的分割线
  &[child=false]::after {
    content: '';
    display: block;
    height: 1px;
    margin-top: 12px;
    background-color: rgba(202, 199, 199, .4);
  }
  .content {
    font-size: 14px;
    line-height: 24px;
    margin: 8px 0;
    :deep(em) {
      margin-right: 6px;
      cursor: pointer;
      color: #00aeec;
    }
  }
}
</style>