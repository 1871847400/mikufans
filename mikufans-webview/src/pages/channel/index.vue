<template>
  <div>
    <nav-bar  />
    <div :class="{nav:y>64}">
      <div class="channel-list">
        <div class="inner">
          <router-link to="/" title="返回首页">
            <i class="iconfont icon-back text-xl px-2 mr-2"></i>
          </router-link>
          <span class="text-xl mr-10">{{ channel?.channelName }}区</span>
          <thumb-tab v-model="option" :options="options" style="gap: 32px" />
        </div>
      </div>
    </div>
    <carousel-view v-if="channel" :channel-id="channel.id" />
    <div v-if="isReady" class="px-[64px] flex gap-8">
      <div class="flex-1">
        <div class="text-lg mb-2 flex items-center gap-1">
          <i class="iconfont icon-lishijilu_o text-2xl"></i>
          <span>最新视频</span>
        </div>
        <Async always :loading="loading" :error="error" :empty="list.length==0" min-h="300px">
          <div class="grid grid-cols-4 lg:grid-cols-3 gap-6 pb-10" v-infinite-scroll="next" :infinite-scroll-disabled="loading||error">
            <template v-for="i in list" :key="i.id">
              <video-item :video="i" />
            </template>
          </div>
        </Async>
        <div v-if="done&&list.length" class="text-center text-xs py-4">已经到底了</div>
      </div>
      <hot-list :channel-id="channelId" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { listVideoChannels } from '@/apis/video/channel';
import videoApi from '@/apis/video';
import { usePage } from '@/hooks/usePage';
import VideoItem from '../home/VideoItem.vue';
import HotList from './HotList.vue';
import CarouselView from './CarouselView.vue';
const route = useRoute()
const router = useRouter()
const { state: channels, isReady } = useAsyncState(listVideoChannels({ child: true }), [])
//当前主分区
const channel = computed(()=>{
  for (const ch of channels.value) {
    if (ch.url == route.path) {
      return ch
    }
    for (const sub of ch.children) {
      if (sub.url == route.path) {
        return ch
      }
    }
  }
})
//当前子分区,可能undefined
const subChannel = computed(()=>channel.value?.children?.find(a=>a.url==route.path))
const option = computed({
  get: () => route.path,
  set(url) { router.push(url) }
})
//分区选项列表
const options = computed(()=>{
  const result = [{ label: '综合', value: channel.value?.url }]
  channel.value?.children?.forEach(a=>{
    result.push({ label: a.channelName, value: a.url })
  })
  return result
})
//当前选中分区,可能为父分区或子分区
const channelId = computed(()=>subChannel.value?.id || channel.value.id)
const { list, run, next, loading, error, done, reset } = usePage(search, {})
function search(pageNum: number) {
  return videoApi.search({
    channelId: channelId.value,
    sort: 'TIME',
    pageNum,
    pageSize: 15
  })
}
watch([channel, subChannel], ()=>{
  if (channel.value) {
    // scrollTo({ y: 0 })
    reset()
    next()
  }
})
const { y } = useWindowScroll()
</script>

<style scoped lang="scss">
$height: var(--navbar-height);
.channel-list {
  padding: 0 64px;
  height: $height;
  box-sizing: border-box;
  overflow: hidden;
  .inner {
    display: flex;
    align-items: center;
    height: $height;
  }
}
.nav {
  position: sticky;
  top: 0;
  z-index: 1000;
  width: 100%;
  height: $height;
  overflow: hidden;
  & > div {
    position: absolute;
    transition: all .3s;
    background-color: var(--bg0);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.07);
    width: 100%;
    bottom: calc($height * -1);
    height: calc($height * 2);
  }
}
</style>