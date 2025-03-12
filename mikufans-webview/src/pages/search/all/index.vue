<template>
  <video-index :keyword="keyword">
    <div v-if="anime?.total" class="mt-4">
      <div class="flex flex-wrap">
        <template v-for="i in anime.records" :key="i.id">
          <anime-search-item class="w-1/2" :video="i" />
        </template>
      </div>
      <div v-if="anime.total>anime.size" class="show-more">
        <button @click="searchType='anime'">查看全部 {{ anime.total>99?'99+':anime.total }} 部相关番剧作品</button>
      </div>
    </div>
    <div v-if="user?.total" class="mt-4">
      <div class="flex flex-wrap">
        <template v-for="i in user.records" :key="i.id">
          <user-search-item class="w-1/2" :user="i" />
        </template>
      </div>
      <div v-if="user.total>user.size" class="show-more">
        <button @click="searchType='user'">查看全部 {{ user.total>99?'99+':user.total }} 名用户</button>
      </div>
    </div>
  </video-index>
</template>

<script setup lang="ts">
import VideoIndex from '../video/index.vue'
import AnimeSearchItem from '../anime/AnimeSearchItem.vue';
import UserSearchItem from '../user/UserSearchItem.vue';
const props = defineProps<{ state: SearchResultCombine, keyword: string }>()
const anime = toRef(()=>props.state?.anime)
const user = toRef(()=>props.state?.user)
const searchType = useRouteParams<string>('type', '')
</script>

<style scoped lang="scss">
.show-more {
  height: 1.5px;
  background-color: #F1F2F3;
  position: relative;
  margin: 24px 0;
  button {
    height: 32px;
    position: absolute;
    left: 50%;
    transform: translateX(-50%) translateY(-50%);
    background-color: #fff;
    border: none;
    cursor: pointer;
    border-radius: 4px;
    color: #00AEEC;
    padding: 0 10px;
    transition: all .3s;
    &:hover {
      background-color: #DFF6FD;
    }
  }
}
</style>