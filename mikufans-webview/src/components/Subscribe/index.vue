<template>
  <button class="subscribe" @click.prevent="subscribe" :title="subscribed ? label : '取消'+label">
    <slot v-if="subscribed" name="active">{{ '已' + label }}</slot>
    <slot v-else name="inactive">{{ label }}</slot>
  </button>
</template>

<script setup lang="ts">
import subscribeApi from '@/apis/bangumi';
const props = defineProps({
  data: object<Bangumi>(),
})
const label = props.data.video?.type=='ANIME' ? '追番' : '追剧'
const userStore = useUserStore()
const subscribed = ref(false) //是否订阅
const submitting = ref(false)
watchEffect(()=>{
  subscribed.value = !!props.data?.subscribed
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
      await subscribeApi.unsubscribe(props.data.id)
      subscribed.value = false
      message.info('已取消' + label)
    } else {
      await subscribeApi.subscribe(props.data.id)
      subscribed.value = true
      message.success(label + '成功')
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.subscribe {
  background: none;
  outline: none;
  cursor: pointer;
}
</style>