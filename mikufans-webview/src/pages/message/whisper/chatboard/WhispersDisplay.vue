<template>
  <div class="flex-1 overflow-hidden">
    <el-scrollbar ref="scrollbarRef" @scroll="onScroll">
      <ul class="p-4">
        <div v-if="done" class="grey2 text-center text-xs mb-5">没有更多历史消息了</div>
        <div v-if="!done&&loading" v-loading="loading"></div>
        <template v-for="i,k in list.toReversed()" :key="i.id">
          <whisper-item :whisper="i" :previous="list[k-1]" />
        </template>
      </ul>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import whisperApi from '@/apis/user/message/whisper'
import { ElScrollbar } from 'element-plus';
import WhisperItem from './WhisperItem.vue';
import { usePage } from '@/hooks/usePage';
const userStore = useUserStore()
const { useSocketEvent } = useMsgStore()
const { targetId } = toRefs(useMsgStore())
const scrollbarRef = ref<InstanceType<typeof ElScrollbar>>()
//监听新消息事件
useSocketEvent('new_whisper', (whisper: UserWhisper)=>{
  //如果新消息是来自当前对话窗口
  if (targetId.value === whisper.userId || targetId.value === whisper.receiverId) {
    list.value.unshift(whisper)
    //如果是自己发送的消息,则滚动到底部
    if (userStore.isSelf(whisper.userId)) {
      scrollTop(0)
    } else {
      //如果是对方发送的消息,但距离底部很近也滚动到底部
      const el = scrollbarRef.value?.wrapRef
      if (Math.abs(el.scrollTop - (el.scrollHeight - el.clientHeight)) < 1) {
        scrollTop(0)
      }
    }
  }
})
useSocketEvent('revoke_whisper', (id: string)=>{
  const data = list.value.find(a=>a.id===id)
  if (data) {
    data.revoked = 1
    data.message = ''
  }
})

function search(pageNum: number, bottom: number) {
  return whisperApi.search({
    targetId: targetId.value,
    pageNum,
    pageSize: 10,
  })
}
const { list, next, done, loading, reset } = usePage(search, {
  shallowRef: false,
  onSuccess(page, bottom) {
    scrollTop(bottom)
  },
})
//注意第一次加载完后,滚动到底部
watchImmediate(targetId, id=>{
  reset() 
  if (id) {
    next(0)
  }
})

//滚动到顶部时获取历史消息
//获取新数据后滚动条会移动到顶部,所以需要手动设置位置
function onScroll({ scrollTop }) {
  const el = scrollbarRef.value?.wrapRef
  if (scrollTop < 5 && el && targetId.value) {
    //距离底部的距离
    const bottom = (el.scrollHeight - el.clientHeight) - el.scrollTop
    next(bottom)
  }
}
//滚动到距离底部多远的位置
//scrollHeight = clientHeight + 最大scrollTop
function scrollTop(bottom: number) {
  nextTick(()=>{
    const el = scrollbarRef.value.wrapRef
    scrollbarRef.value.scrollTo({
      behavior: 'instant',
      left: 0,
      top: el.scrollHeight - el.clientHeight - bottom,
    })
  })
}
</script>

<style scoped lang="scss">
</style>