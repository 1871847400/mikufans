<template>
  <div>
    <div class="flex items-center justify-between p-3">
      <span class="font-bold grey0">搜索历史</span>
      <text-button @click="clear" tabindex="-1">清空</text-button>
    </div>
    <div ref="el" class="flex flex-wrap gap-x-4 gap-y-3 px-3 py-1 text-sm">
      <div v-if="list.length===0" class="grey2 text-center flex-1">无搜索记录</div>
      <transition-group v-else>
        <template v-for="i in list" :key="i">
          <text-button class="bg1 py-2 px-3" @click="search(i)">
            <span class="truncate max-w-[160px] text-xs">{{ i }}</span>
            <i class="iconfont absolute -right-1 -top-1 grey3 hidden" @click.stop="remove(i)">&#xe648;</i>
          </text-button>
        </template>
      </transition-group>
    </div>
    <div class="p-1" v-if="overflow">
      <text-button class="mx-auto" @click="expand=!expand" tabindex="-1">{{ expand ? '收起' : '展开更多' }}</text-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useOverflow } from '@/hooks/useOverflow';
const el = ref<HTMLElement>()
const { search, remove, clear } = useSearchStore()
const { list } = storeToRefs(useSearchStore())
const { expand, overflow } = useOverflow(el, 135)
</script>

<style scoped lang="scss">
.text-button:hover .iconfont {
  display: block;
}
.v-enter-active,
.v-leave-active {
  transition: all 0.2s ease;
}
.v-enter-from,
.v-leave-to {
  opacity: 0;
  transform: scale(0);
}
</style>