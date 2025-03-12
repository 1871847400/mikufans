<template>
  <div class="menu">
    <thumb-tab v-model="option" :options="options" automove style="gap: 32px; height: 60px;">
      <template v-slot="{ label, icon }">
        <div class="flex items-center">
          <i class="iconfont tab-icon" :class="icon.class" :style="{color:icon.color}"></i>
          <span>{{ label }}</span>
        </div>
      </template>
    </thumb-tab>
    <search-box/>
    <div class="flex items-center gap-4 ml-auto">
      <template v-for="{ path, label, count } in stats" :key="label">
        <router-link class="nav-link" :to="path" :data-path="path" draggable="false">
          <div class="grey1 whitespace-nowrap">{{ label }}</div>
          <div class="font-bold">{{ displayNumber(count) }}</div>
        </router-link>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { displayNumber } from '@/utils/common';
import { createOptions } from './options';
import SearchBox from './SearchBox.vue';
const { isSelf, userId, user } = toRefs(useSpaceStore())
const { isAllowed } = useSpaceStore()
const route = useRoute()
const router = useRouter()
const options = computed(()=>{
  return createOptions(isSelf.value, isAllowed)
})
const option = computed<string>({
  get() {
    return route.path
      .replace(`/space/${userId.value}`, '')
      .replace('/', '')
  },
  set(name) {
    if (name=='') {
      router.push(`/space/${userId.value}`)
    } else {
      router.push(`/space/${userId.value}/${name}`)
    }
  }
})
const stats = computed(()=>{
  if (!user.value) {
    return []
  }
  return [
    {
      label: '关注数',
      count: user.value.follows,
      path: isAllowed('PUBLIC_FOLLOW') ? `/space/${user.value.id}/follow` : '',
    },
    {
      label: '粉丝数',
      count: user.value.fans,
      path: isAllowed('PUBLIC_FANS') ? `/space/${user.value.id}/fans` : '',
    },
    {
      label: '获赞数',
      count: user.value.likes,
      path: '',
    },
    {
      label: '获币数',
      count: user.value.coins,
      path: '',
    },
  ]
})
</script>

<style scoped lang="scss">
.menu {
  background-color: var(--bg0);
  position: relative; //提高层级
  border-radius: 0 0 8px 8px;
  display: flex;
  align-items: center;
  padding: 0 32px;
  .tab-icon {
    margin-right: 6px;
    font-size: 18px;
  }
}
.nav-link {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: center;
  font-size: 13px;
  cursor: auto;
  &[data-path^='/'] {
    cursor: pointer;
    &.router-link-active div {
      color: var(--blue0);
    }
  }
}
</style>