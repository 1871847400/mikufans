<template>
  <div v-if="likeData">
    <div class="bg0 shadow mb-4 p-4 rounded-lg flex items-center">
      <span class="mr-2 shrink-0">{{ likeData.likeLabel }}:</span>
      <a :href="likeData.source.uri" target="_blank">
        <rich-text html :content="likeData.source.message" :rows="1" />
      </a>
    </div>
    <Async always :loading="loading" :error="error">
      <ul v-infinite-scroll="next" :infinite-scroll-disabled="loading||error" class="grid grid-cols-5 gap-2">
        <li v-for="{ user, likeTime } in list" :key="user.id" class="flex gap-1 rounded-2xl p-2 bg0">
          <user-avatar :user="user" size="40px" />
          <div>
            <div class="flex gap-2">
              <a :href="user.uri" target="_blank">{{ user.nickname }}</a>
              <span>赞了我</span>
            </div>
            <div class="grey2 text-xs py-1">{{ likeTime }}</div>
          </div>
        </li>
      </ul>
    </Async>
  </div>
</template>

<script setup lang="ts">
import userLikeApi from '@/apis/user/like';
import { usePage } from '@/hooks/usePage';
const props = defineProps<{ id: string }>()
const { state: likeData } = useAsyncState(userLikeApi.getLikeData(props.id), null)
function search(pageNum: number) {
  return userLikeApi.getLikeUsers(props.id, { pageNum })
}
const { list, next, loading, error, } = usePage(search)
</script>

<style scoped lang="scss">
</style>