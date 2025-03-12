<template>
  <div class="flex-1 overflow-hidden">
    <el-scrollbar class="pr-3">
      <div v-if="list.length==0&&done" class="p-12 text-center bg0 rounded-lg">没有收到过赞</div>
      <ul v-else v-infinite-scroll="next" :infinite-scroll-disabled="loading||error">
        <transition-group>
          <template v-for="i in list" :key="i.id">
            <router-link :to="'/msg/like/' + i.id">
              <like-msg :data="i" />
            </router-link>
          </template>
        </transition-group>
      </ul>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import userLikeApi from '@/apis/user/like';
import LikeMsg from './LikeMsg.vue';
function search(pageNum: number) {
  return userLikeApi.getMsg({
    pageNum,
    pageSize: 10,
  })
}
const { list, next, done, loading, error } = usePage(search, {
  shallowRef: false,
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