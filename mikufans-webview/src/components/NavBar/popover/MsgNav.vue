<template>
  <div class="nav-list">
    <template v-for="nav in navList" :key="nav[1]">
      <router-link class="nav-link" :to="nav[1]" :count="nav[2]">{{ nav[0] }}</router-link>
    </template>
  </div>
</template>

<script setup lang="ts">
const { msgUnread: unreadCounts } = toRefs(useMsgStore())
const navList = computed(()=>[
  ['回复我的', '/msg/reply', unreadCounts.value.reply],
  ['@我的', '/msg/at', unreadCounts.value.atuser],
  ['收到的赞', '/msg/favor', unreadCounts.value.likes],
  ['系统消息', '/msg/system', unreadCounts.value.systems],
  ['我的消息', '/msg/whisper', unreadCounts.value.whisper],
])
</script>

<style scoped lang="scss">
.nav-list {
  padding: 12px 0;
  .nav-link {
    display: block;
    padding: 12px 24px;
    transition: all .3s;
    position: relative;
    &:hover {
      background-color: var(--bg2);
    }
    &[count='0']::after {
      display: none;
    }
    &::after {
      content: attr(count);
      font-size: 10px;
      color: #fff;
      width: 20px;
      padding: 1px 0;
      text-align: center;
      position: absolute;
      background-color: #fa5a57;
      border-radius: 50%;
      right: 30px;
      top: 50%;
      transform: translateY(-50%);
    }
  }
}
</style>