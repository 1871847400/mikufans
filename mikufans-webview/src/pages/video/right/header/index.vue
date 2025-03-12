<template>
  <div class="flex mt-5 mb-3" v-if="author">
    <user-avatar :user="author" size="65px"/>
    <div class="ml-3 flex-1">
      <a class="text-lg maxline-1 w-fit" :href="'/space/'+ author.id" target="_blank">{{ author.nickname }}</a>
      <div class="text-xs maxline-1 grey2 my-1">{{ author.sign || '个性签名' }}</div>
      <div class="flex">
        <user-follow-button 
          style="min-width: 125px;" 
          :user-id="author.id" 
          :fans="author.fans" 
          :status="author.follow" 
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const videoStore = useVideoStore()
const author = computed(()=>videoStore.video?.user)
</script>