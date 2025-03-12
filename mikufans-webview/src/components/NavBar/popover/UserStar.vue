<template>
  <div class="flex">
    <el-scrollbar>
      <ul class="menu">
        <template v-for="item,index in userStars" :key="item.id">
          <li :active="active===index" @click="active=index">
            <span class="flex-1 maxline-1">{{ item.starName }}</span>
            <span class="w-8 text-right grey2 text-sm">{{ item.starCount }}</span>
          </li>
        </template>
      </ul>
    </el-scrollbar>
    <div class="flex-1">
      <el-scrollbar>
        <ul class="video-list" 
          v-infinite-scroll="next"
          :infinite-scroll-disabled="loading||error" 
          :infinite-scroll-immediate="false"
        >
          <span class="m-auto" v-if="loading">加载中...</span>
          <span class="m-auto" v-else-if="error">加载失败！</span>
          <span class="m-auto" v-else-if="list.length==0">收藏夹内容为空</span>
          <template v-else v-for="i in list" :key="i.id">
            <video-list-item class="video-item" :video="i.video" />
          </template>
        </ul>
      </el-scrollbar>
    </div>
  </div>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import userStarApi from '@/apis/user/star';
import videoStarApi from '@/apis/video/star';
const { state: userStars } = useAsyncState(userStarApi.search({}), [], {
  onSuccess() {
    next()
  }
})
function searchList(pageNum: number) {
  return videoStarApi.search({
    starId: userStars.value[active.value].id,
    pageNum,
    pageSize: 10,
  })
}
const { list, next, reset, loading, error } = usePage(searchList,)
const active = ref(0)
watch(active, ()=>{
  reset()
  next()
})
</script>

<style scoped lang="scss">
$height: 520px;
ul.menu {
  color: var(--grey0);
  width: 180px;
  height: $height;
  box-sizing: border-box;
  padding: 12px 0;
  border-right: 1px solid #ccc;
  font-size: 16px;
  li {
    user-select: none;
    cursor: pointer;
    box-sizing: border-box;
    width: 100%;
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-left: 12px;
    padding-right: 20px;
    transition: all .3s;
    &[active='true'] {
      background-color: var(--blue0);
      span {
        color: #fff;
      }
    }
  }
}
ul.video-list {
  height: $height;
  display: flex;
  flex-direction: column;
  padding: 12px 0;
  .video-item {
    transition: all .3s;
    padding: 8px 16px;
    margin: 0;
    &:hover {
      background-color: var(--bg2);
    }
  }
}
</style>