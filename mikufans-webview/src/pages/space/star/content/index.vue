<template>
  <div class="index">
    <div class="flex gap-4" v-if="selectStar">
      <div @click="edit" class="cursor-pointer" title="编辑收藏夹">
        <miku-image :res-id="selectStar.coverId" video class="w-[200px]"  />
      </div>
      <div class="flex flex-col">
        <div class="text-base pb-2">{{ selectStar.starName }}</div>
        <div class="text-xs grey2">{{ selectStar.intro }}</div>
        <div class="text-sm mt-4 grey1">
          <span class="mr-2">{{ selectStar.starCount }}个内容</span>
          <span>{{ selectStar.starDesc }}</span>
        </div>
        <div class="mt-auto">
          <span class="grey1 cursor-pointer" :class="{active:selectStar.likeStatus.likeVal==1}" @click="like" title="点赞">
            <i class="iconfont icon-dianzan_kuai mr-1"></i>
            <span>{{ selectStar.likeStatus.likes }}</span>
          </span>
        </div>
      </div>
    </div>
    <el-divider class="mt-4 mb-2"/>
    <div class="flex justify-end mb-2 gap-6">
      <simple-tab v-model:index="sort" type="text" class="text-[13px]" :options="['最晚收藏','最早收藏']"/>
      <search-box :modelValue="keyword" @search="keyword=$event" />
    </div>
    <List :keyword="keyword" :sort="sort" />
  </div>
</template>

<script setup lang="ts">
import { useLike } from '@/hooks/useLike';
import List from './List.vue';
import { useStore } from '../store';
const { isSelf } = toRefs(useSpaceStore())
const { saveUserStar, selectStar } = useStore()
const keyword = useRouteQuery('kw', '')
const sort = useRouteQuery('sort', 0)
//注意此时selectStar可能为null
const { like } = useLike(toRef(()=>selectStar.value?.likeStatus))
function edit() {
  if (isSelf.value) {
    saveUserStar(selectStar.value)
  }
}
</script>

<style scoped lang="scss">
.index {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0 12px;
  padding-bottom: 120px;
}
.active {
  color: var(--blue0);
}
</style>