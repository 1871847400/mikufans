<template>
  <div class="bg0 rounded-lg p-4 box-border h-fit">
    <div class="flex items-center gap-2">
      <user-avatar :user="userStore.$state" size="60px" :popover="false"/>
      <div>
        <div class="text-lg font-semibold">{{ userStore.nickname }}</div>
        <svg-icon class="-mt-3" :name="'level_'+userStore.level" :size="36"/>
      </div>
    </div>
    <div class="flex items-center justify-between mt-4 px-3">
      <template v-for="i in list">
        <a class="flex flex-col gap-1 items-center" :href="i[2]" target="_blank">
          <span class="font-bold">{{ i[0] }}</span>
          <span class="grey2 text-sm">{{ i[1] }}</span>
        </a>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
const userStore = useUserStore()
const list = computed<[number,string,string][]>(()=>[
  [userStore.follows, '关注', `/space/${userStore.id}/follow`],
  [userStore.fans, '粉丝', `/space/${userStore.id}/fans`],
  [userStore.dynamics, '动态', `/space/${userStore.id}/dynamic`],
])
</script>

<style scoped lang="scss">
</style>