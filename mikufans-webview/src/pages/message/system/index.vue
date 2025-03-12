<template>
  <el-scrollbar class="flex-1">
    <div v-if="list.length==0" class="bg0 p-8 text-center rounded-lg">通知为空</div>
    <ul v-else v-infinite-scroll="next" :infinite-scroll-disabled="loading||error" class="pr-4">
      <transition-group>
        <template v-for="i in list" :key="i.id">
          <li class="bg0 p-5 rounded-lg shadow-xl mb-2">
            <a class="font-bold inline-block mb-2" :href="i.uri||null" target="_blank">{{ i.title }}</a>
            <rich-text class="text-sm grey2 mb-2" :content="i.content" html :rows="3" />
            <div class="grey2 text-xs text-right">
              <a v-if="i.uri" class="blue0" :href="i.uri" target="_blank">网页链接</a>
              <span class="ml-4">{{ i.publishTime }}</span>
            </div>
          </li>
        </template>
      </transition-group>
    </ul>
  </el-scrollbar>
</template>

<script setup lang="ts">
import userNoticeApi from '@/apis/user/notice'
import { usePage } from '@/hooks/usePage';
function search(pageNum: number) {
  return userNoticeApi.search({
    pageNum,
    pageSize: 10
  })
}
const { list, next, loading, done, error } = usePage(search, {
  immediate: [1],
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