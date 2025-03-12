<template>
  <ul class="nav-area">
    <li class="mr-6">
      <a class="text" href="/" title="Mikufans" draggable="false">
        <img class="w-[120px]" :src="Logo" alt="Mikufans" draggable="false">
      </a>
    </li>
    <li>
      <el-popover v-model:visible="visible" trigger="hover" width="auto">
        <template #reference>
          <a class="text" href="/" title="首页" draggable="false">
            <span>首页</span>
            <i class="iconfont icon-jiantoushang ml-1 text-[10px] transition-all" :class="{'-rotate-180':visible}"></i>
          </a>
        </template>
        <!-- 鼠标悬浮显示频道列表 -->
        <channel-list/>
      </el-popover>
    </li>
    <li>
      <a class="text" href="/anime" target="_blank" title="番剧" draggable="false">番剧</a>
    </li>
    <li>
      <a class="text" href="/movie" target="_blank" title="电影" draggable="false">电影</a>
    </li>
    <li>
      <a class="text" href="/index" target="_blank" title="索引" draggable="false">索引</a>
    </li>
    <!-- <li>
      <a class="text" href="#">直播</a>
      <div class="popover">
        <div class="live">
          <section class="live-left">
            <h1>热门直播:</h1>
          </section>
          <section class="live-right">
            <h1>热门活动:</h1>
          </section>
        </div>
      </div>
    </li> -->
  </ul>
</template>

<script setup lang="ts">
import Logo from '@/assets/images/logo.png'
import ChannelList from './ChannelList.vue';
const visible = ref(false)
</script>

<style scoped lang="scss">
a {
  user-select: none;
}
ul.nav-area {
  display: flex;
  list-style: none;
  column-gap: 16px;
}
ul.nav-area > li {
  white-space: nowrap;
  height: 56px;
  line-height: 56px;
  font-size: 14px;
  position: relative;
  &:hover:nth-child(n+3) a.text { //nth-child除去第1,2个元素
    animation: jump 0.5s;
  }
  &:hover .rotate {
    transform: rotate(180deg);
  }
  a.text {
    display: flex;
    // font-weight: bold;
    height: 100%;
    color: inherit;
    text-decoration: none;
    align-items: center;
  }
  @keyframes jump {
    50% {
      transform: translateY(-5px);
    }
  }
}
.rotate {
  transition: transform .5s;
}
</style>