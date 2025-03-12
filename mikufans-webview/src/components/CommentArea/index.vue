<template>
  <div>
    <div class="mt-4 flex items-center gap-8">
      <div>
        <span class="text-xl mr-1">评论</span>
        <span class="grey2">{{ page?.total || 0 }}</span>
      </div>
      <div>
        <el-button link :class="{grey: order==0, grey2: order!=0}" @click="order=0">最热</el-button>
        <el-divider direction="vertical"/>
        <el-button link :class="{grey: order==1, grey2: order!=1}" @click="order=1">最新</el-button>
      </div>
      <div v-if="area.commentFlag==='CHOICE'" style="color: rgb(255, 91, 203);">当前评论区已开启精选评论</div>
    </div>
    <comment-send/>
    <div class="mt-4 relative" v-infinite-scroll="next" :infinite-scroll-disabled="loading">
      <transition-group name="comment-group">
        <template v-for="i in showList" :key="i.id">
          <comment-item :comment="i"/>
        </template>
      </transition-group>
      <el-skeleton animated :loading="loading" :count="skeletonCount">
        <template #template>
          <div class="skeleton-comment">
            <el-skeleton-item class="size-[50px] shrink-0 mr-3" variant="circle"/>
            <div class="body">
              <el-skeleton-item class="w-[10%] mb-2" variant="caption"/>
              <div class="mt-3">
                <el-skeleton-item class="w-4/5 mb-1" variant="p"/>
                <el-skeleton-item class="w-3/5" variant="p"/>
              </div>
              <div class="flex">
                <el-skeleton-item class="w-[18%] mr-4" variant="text"/>
                <el-skeleton-item class="w-[15%] mr-4" variant="text"/>
                <el-skeleton-item class="w-10" variant="text"/>
                <el-skeleton-item class="w-4 ml-auto" variant="text"/>
              </div>
            </div>
          </div>
        </template>
      </el-skeleton>
      <div v-if="done" class="text-center text-sm grey3">已经到底了</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import CommentSend from './CommentSend.vue';
import CommentItem from './CommentItem.vue';
import commentApi from '@/apis/user/comment';
import { clamp } from 'lodash';
import { useProvideStore, useStore, StoreParams } from './store';
//skeleton: 初次请求显示的骨架屏数量
const props = defineProps<StoreParams & { skeleton?: number }>()
//注入store
useProvideStore(props)
const hash = useRoute().hash
const { useHash, request, order, sendList, removeList, pageSize } = useStore()
const { page, list, next, loading, done } = request
const showList = computed(()=>{
  const result = [...list.value]
  result.unshift(...sendList.value.filter(a=>a.pid==='0'))
  return result.filter(a=>!removeList.value.find(b=>a.id===b.id))
})
next()
//如果定位了评论
if (useHash && hash) {
  const id = hash.replace('#', '')
  if (id) {
    commentApi.getTreeById(id).then(data=>{
      if (data && list.value.findIndex(a=>a.id===data.id) === -1) {
        list.value.unshift(data)
      }
    })
  }
}
const skeletonCount = computed(()=>{
  if (page.value) {
    return clamp(page.value.total - list.value.length, 0, page.value.size)
  }
  if (Number.isInteger(props.skeleton)) {
    return Math.min(pageSize, props.skeleton)
  }
  return Math.min(props.area.comments, pageSize)
})
</script>

<style scoped lang="scss">
.skeleton-comment {
  margin-bottom: 32px;
  display: flex;
  .body {
    flex: 1;
    * {
      display: block;
    }
    &::after {
      content: '';
      display: block;
      height: 1px;
      margin-top: 12px;
      background-color: rgba(202, 199, 199, .4);
    }
  }
}
.comment-group-enter-active,
.comment-group-leave-active {
  transition: all .3s ease;
}
.comment-group-leave-active {
  position: absolute;
}
.comment-group-enter-from,
.comment-group-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>