<template>
  <li class="bg0 flex px-4 py-2 mb-2 gap-4 items-center rounded-lg overflow-hidden">
    <user-avatar @click.stop v-if="data.likeUsers.length<=1" :user="data.likeUsers[0]" size="48px" />
    <div class="avatar-over" v-else>
      <template v-for="user in data.likeUsers.slice(0, 2)" :key="user.id">
        <user-avatar @click.stop :user="user" size="32px" />
      </template>
    </div>
    <div class="flex-1">
      <span class="nicknames font-bold">
        <a @click.stop v-for="{ nickname, uri } in data.likeUsers" :href="uri" target="_blank">{{ nickname }}</a>
      </span>
      <span class="inline-flex">
        <span v-if="data.likes>1">等总计{{ data.likes }}人</span>
        <span>
          <span>赞了我的{{data.likeLabel}}</span>
          <span v-if="!data.source" class="grey2 italic">(已失效)</span>
        </span>
        <router-link
          v-if="data.source"
          @click.stop
          class="blue0 mx-2 truncate max-w-xs" 
          :to="data.source.uri" 
          target="_blank"
        >
          {{ data.source.message }}
        </router-link>
      </span>
    </div>
    <span class="grey2 flex-shrink-0">{{ data.likeTimeStr }}</span>
  </li>
</template>

<script setup lang="ts">
const props = defineProps<{ data: UserLikeData }>()
</script>

<style scoped lang="scss">
.avatar-over {
  /* 宽高和单个头像大小保持一致 */
  width: 48px;
  height: 48px;
  position: relative;
  :deep(.user-avatar) {
    position: absolute;
  }
  :deep(.user-avatar:nth-of-type(1)) {
    left: 0;
    top: 0;
  }
  :deep(.user-avatar:nth-of-type(2)) {
    right: 0;
    bottom: 0;
  }
}
.nicknames {
  a {
    margin-right: 4px;
    transition: color .3s;
  }
  a:hover {
    color: var(--blue0);
  }
  a::after {
    content: '/';
    color: var(--grey2);
    padding: 0 4px;
  }
  a:last-child::after {
    display: none;
  }
}
</style>