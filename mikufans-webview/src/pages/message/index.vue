<template>
  <div class="background">
    <nav-bar transparent/>
    <main>
      <message-tab/>
      <div class="content">
        <router-view v-slot="{ Component, route }">
          <div class="bg0 rounded-lg p-3 mb-3 flex gap-2">
            <template v-for="{ title, path } in navList">
              <router-link :to="path">{{ title }}</router-link>
            </template>
          </div>
          <!-- <keep-alive> -->
            <component :is="Component" :key="route.path"/>
          <!-- </keep-alive> -->
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import MessageTab from './MessageTab.vue';
const route = useRoute()
const navList = computed(()=>{
  const result = []
  for (const matched of route.matched.slice(1)) {
    const paths = []
    for (const segment of matched.path.split('/')) {
      //如果该路径段使用了正则,则使用真实地址的对应位置
      if (segment.startsWith(':')) {
        paths.push(route.path.split('/')[paths.length])
      } else {
        paths.push(segment)
      }
    }
    result.push({
      title: matched.meta.title,
      path: paths.join('/'),
    })
  }
  return result
})
</script>

<style scoped lang="scss">
.background {
  height: 100vh;
  background-image: url('/src/assets/images/msg_bg.jpg');
  background-repeat: no-repeat;
  background-size: cover;
  /* background-position: 50%; */
  background-position: 50% -100px;
  overflow: hidden;
  // background-attachment: scroll;
}
main {
  width: 70%;
  min-width: 900px;
  margin: 0 auto;
  display: flex;
  height: calc(100% - var(--navbar-height));
  .content {
    flex: 1;
    height: calc(100% - 16px);
    min-height: 500px;
    display: flex; //方便子容器撑满高度
    flex-direction: column;
    background-color: #a8d1e2cb;
    padding: 10px;
  }
}
</style>