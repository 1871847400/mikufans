<template>
  <div class="message-tab">
    <div class="flex items-center justify-center font-bold my-3 gap-2">
      <i class="iconfont icon-24gl-bell"></i>
      <span>消息中心</span>
    </div>
    <div class="tab-list">
      <template v-for="{ title, path, count } in menus" :key="path">
        <router-link 
          class="tab-link" 
          active-class="active" 
          :to="path"
          :data-count="count"
        >
          {{ title }}
        </router-link>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
const { msgUnread } = storeToRefs(useMsgStore())
const menus = computed(()=>[
  { title: '我的消息', path: '/msg/whisper', count: msgUnread.value.whisper },
  { title: '回复我的', path: '/msg/reply', count: msgUnread.value.reply },
  { title: '@我的', path: '/msg/at', count: msgUnread.value.atuser },
  { title: '收到的赞', path: '/msg/like', count: msgUnread.value.likes },
  { title: '系统通知', path: '/msg/system', count: msgUnread.value.systems },
])
</script>

<style scoped lang="scss">
.message-tab {
  height: 100%;
  background-color: #ffffffeb;
  html.dark & {
    background-color: #333333e2;
  }
}
.tab-link {
  width: 150px;
  height: 50px;
  line-height: 50px;
  box-sizing: border-box;
  padding: 0 16px;
  user-select: none;
  display: block;
  cursor: pointer;
  position: relative;
  transition: all .3s;
  font-weight: 550;
  color: var(--grey1);
  &:hover, &.active {
    color: var(--blue0);
  }
  &::before {
    content: '●';
    margin-right: 8px;
  }
  &[data-count='0']::after {
    display: none;
  }
  &::after {
    content: attr(data-count);
    font-size: 10px;
    display: inline-block;
    white-space: nowrap;
    color: #fff;
    position: absolute;
    text-align: center;
    background-color: #fa5a57;
    width: 16px;
    height: 16px;
    line-height: 16px;
    top: 50%;
    transform: translateY(-50%);
    border-radius: 45%;
    right: 28px;
  }
}
</style>