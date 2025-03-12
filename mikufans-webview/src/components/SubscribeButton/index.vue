<template>
  <!-- 按钮类型 -->
  <div v-if="type==='button'" @click="subscribe" :title="label" class="subscribe-button button" :data-active="subscribed">
    <i class="iconfont icon-shoucang4"></i>
    <span>{{ subscribed ? '已' + label : label }}</span>
  </div>
  <!-- 图标类型 -->
  <div v-else-if="type==='icon'" @click="subscribe" :title="label" class="subscribe-button iconfont">
    {{ subscribed ? '&#xe613;' : '&#xe643;' }}
  </div>
</template>

<script setup lang="ts">
import subscribeApi from '@/apis/bangumi';
const props = defineProps({
  //按钮类型
  type: string<'button'|'icon'>().def('button'),
  //按钮和提示的名称
  label: string().def('追番'),
  //节目数据
  bangumi: object<Bangumi>(),
})
const userStore = useUserStore()
const subscribed = ref(false) //是否订阅
const submitting = ref(false)
watchEffect(()=>{
  subscribed.value = !!props.bangumi?.subscribed
})
async function subscribe() {
  if (!userStore.isLogin) {
    return userStore.login()
  }
  if (submitting.value) {
    return
  }
  submitting.value = true
  try {
    if (subscribed.value) {
      await subscribeApi.unsubscribe(props.bangumi.id)
      subscribed.value = false
      message.info('已取消' + props.label)
    } else {
      await subscribeApi.subscribe(props.bangumi.id)
      subscribed.value = true
      message.success(props.label + '成功！')
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.subscribe-button.button {
  color: #fff;
  width: 100px;
  height: auto;
  aspect-ratio: 100/35;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background-color: var(--blue0);
  cursor: pointer;
  user-select: none;
  transition: all .3s;
  &[data-active=false]:hover {
    filter: brightness(1.1);
  }
  &[data-active=true] {
    background-color: var(--grey2);
  }
}
.subscribe-button.iconfont {
  width: 50px;
  height: auto;
  aspect-ratio: 1;
  white-space: nowrap;
  border-radius: 50%;
  background-color: rgba(0,0,0,.6);
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  cursor: pointer;
  transition: scale 0.3s;
  &:hover {
    scale: 1.2;
  }
}
</style>