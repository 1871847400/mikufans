<template>
  <div class="py-4 flex">
    <el-menu router :default-active="type">
      <template v-for="{ hash, label, value } in countMap" :key="hash">
        <el-menu-item :index="hash" :route="{ hash }">
          <span>{{ label }}</span>
          <span class="ml-auto">{{ value || 0 }}</span>
        </el-menu-item>
      </template>
    </el-menu>
    <video-list v-if="type==='#video'" :userId="user.id" />
    <article-list v-else-if="type==='#article'" />
  </div>
</template>

<script setup lang="ts">
import { useRouteHash } from '@vueuse/router';
import VideoList from './VideoList.vue';
import ArticleList from './ArticleList.vue';
const type = useRouteHash('#video')
const { user } = toRefs(useSpaceStore())
const countMap = computed(()=>[
  {
    label: '视频',
    value: user.value.videos,
    hash: '#video'
  },
  {
    label: '文章',
    value: user.value.articles,
    hash: '#article'
  },
])
</script>

<style scoped lang="scss">
.el-menu-item {
  color: var(--grey0);
  width: 160px;
  &.is-active {
    background-color: var(--blue0);
    color: #fff;
  }
}
</style>