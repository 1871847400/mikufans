<template>
  <div>
    <div class="text-center py-4 mt-4 grey2">其它方式登录</div>
    <div class="flex items-center gap-6">
      <template v-for="{ title, icon, href } in authList" :key="title">
        <a :href="href" class="flex-center gap-2">
          <svg-icon :name="icon" :size="24"/>
          <span>{{ title }}</span>
        </a>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import loginApi from '@/apis/security/login'
const authList = reactive({
  github: {
    title: 'GitHub',
    icon: 'github-logo',
    href: null
  },
  google: {
    title: 'Google(测试)',
    icon: 'google-logo',
    href: null
  }
})
loginApi.listAuth().then(data=>{
  for (const key in data) {
    if (authList[key]) {
      authList[key].href = data[key] 
    }
  }
})
</script>

<style scoped lang="scss">
</style>