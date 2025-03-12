<template>
  <aside class="hotlist relative">
    <div class="header">
      <i class="iconfont icon-gengduo5"></i>
      <span>排行榜</span>
    </div>
    <ol class="hot-list" v-if="page">
      <div v-if="page?.empty" class="grey2">还没有人发布过这种视频</div>
      <template v-else v-for="i in page.records" :key="i.id">
        <video-list-item :video="i"></video-list-item>
        <!-- <li class="hot-item">
          <a class="flex items-center" :title="i.title" :href="'/video/'+i.sid" target="_blank">
            <template v-if="idx==0">
              <miku-image class="flex-shrink-0 w-[90px] mr-1 my-4" video :res-id="i.bannerId" style="border-radius: 4px;" />
              <span class="maxline-3">{{ i.title }}</span>
            </template>
            <span v-else class="maxline-1">{{ i.title }}</span>
          </a>
        </li> -->
      </template>
    </ol>
  </aside>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import videoApi from '@/apis/video';
const props = defineProps({
  channelId: string().isRequired,
})
const { page, run, loading, error } = usePage(search)
function search(pageNum: number) {
  return videoApi.search({
    channelId: props.channelId,
    sort: 'SCORE',
    pageNum,
    pageSize: 10,
    searchCount: false,
  })
}
watchImmediate(()=>props.channelId, ()=>run(1))
</script>

<style scoped lang="scss">
.header {
  font-size: 18px;
  line-height: 1.5;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
  .iconfont {
    font-size: 16px;
  }
  /* position: absolute;
  left: 0;
  top: -4px;
  transform: translateY(-100%); */
}
.hot-list {
  /* border: 1px solid var(--bg1);
  border-radius: 6px; */
  width: 300px;
}
.video-list-item {
  position: relative;
  counter-increment: order 1;
  &::before {
    content: counter(order);
    position: absolute;
    color: #fff;
    left: 0;
    top: 0;
    width: 14px;
    top: 4px;
    border-top-left-radius: 4px;
    text-align: center;
    background-color: #ccc;
    z-index: 1;
  }
  &:nth-of-type(1)::before {
    background-color: #fc5543;
  }
  &:nth-of-type(2)::before {
    background-color: #ff6a29;
  }
  &:nth-of-type(3)::before {
    background-color: #ff9216;
  }
}
/* .hot-item {
  min-height: 50px;
  display: flex;
  align-items: center;
  &:nth-of-type(2n) {
    background-color: var(--bg1);
  }
  &:nth-of-type(1)::before {
    color: #ff7969;
  }
  &:nth-of-type(2)::before {
    color: #ff6a29;
  }
  &:nth-of-type(3)::before {
    color: #ff9216;
  }
  counter-increment: order 1;
  &::before {
    content: counter(order);
    width: 40px;
    text-align: center;
    font-style: italic;
    font-weight: 550;
    color: var(--grey2);
    flex-shrink: 0;
  }
  a {
    line-height: 1.35;
    font-size: 14px;
    transition: color .3s;
    &:hover {
      color: var(--blue0);
    }
  }
} */
</style>