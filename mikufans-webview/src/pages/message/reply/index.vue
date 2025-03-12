<template>
  <div class="flex-1 rounded-lg overflow-hidden bg0">
    <el-scrollbar>
      <div v-infinite-scroll="next" :infinite-scroll-disabled="loading||error">
        <Async always :loading="loading" :empty="list.length==0" :error="error" min-h="500px">
          <transition-group>
            <template v-for="i in list" :key="i.id">
              <reply-item :msg="i" :at="at"/>
            </template>
          </transition-group>
        </Async>
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import commentMsgApi from '@/apis/user/comment/msg';
import ReplyItem from './ReplyItem.vue';
const props = defineProps<{ at?: boolean }>()
async function search(pageNum: number) {
  return await commentMsgApi.search({
    msgType: props.at ? 'AT_USER' : 'REPLY',
    pageNum,
    pageSize: 8,
  })
}
const { list, next, loading, error, reset } = usePage(search, {
  shallowRef: false,
  immediate: [1]
})
</script>

<style scoped lang="scss">
.v-enter-active,
.v-leave-active {
  transition: all 0.3s ease;
  transition-delay: .3s;
}
.v-enter-from,
.v-leave-to {
  opacity: 0;
  transform: translateY(60px);
}
</style>