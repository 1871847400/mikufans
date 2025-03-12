<template>
  <div ref="el" title="更换背景"
    class="banner-select" 
    @mouseenter="play(true)" 
    @mouseleave="play(false)" 
    @click="drawer=!drawer">
  </div>
  <el-drawer 
    v-model="drawer" 
    :with-header="false" 
    direction="btt" 
    :lock-scroll="false"
  >
  <el-scrollbar style="background: #333;">
    <div class="image-list">
      <template v-for="path,name in backgrounds" :key="path">
        <div class="image-item">
          <div class="image" :title="'点击更换为:'+name" @click="change(name)" :style="{backgroundImage: `url(${baseURL}${path})`}"></div>
          <div class="flex justify-between p-2">
            <span>{{ name }}</span>
            <el-button 
              size="small" 
              type="primary" 
              @click="change(name)" 
              :disabled="background==name"
            >
              {{ background==name ? '使用中' : '使用' }}
            </el-button>
          </div>
        </div>
      </template>
    </div>
  </el-scrollbar>
  </el-drawer>
</template>

<script setup lang="ts">
import { baseURL } from '@/apis/service'
import userApi from '@/apis/user';
const { background, backgrounds, id } = toRefs(useUserStore())
const drawer = ref(false)
const el = ref<HTMLElement>()
let index = 0
function play(enter = true) {
  function render() {
    if (enter) {
      index++
    } else {
      index = Math.max(0, index - 1)
    }
    el.value.style.backgroundPositionX = index * -el.value.clientWidth + 'px'
    if (index < 9 && index > 0) {
      requestAnimationFrame(render)
    }
  }
  render()
}
async function change(val: string) {
  await userApi.update({
    id: id.value,
    background: val
  })
  background.value = val
}
</script>

<style scoped lang="scss">
.banner-select {
  background-image: url('@/assets/images/theme-trigger-new.png');
  background-repeat: no-repeat;
  top: 0;
  right: 0;
  width: 58px;
  height: 49px;
  position: absolute;
  cursor: pointer;
}
.image-list {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(4, 1fr);
  width: 82%;
  min-width: 1000px;
  margin: 0 auto;
  padding: 16px 0;
  .image-item {
    border-radius: 8px;
    padding: 4px;
    background-color: #fff;
    .image {
      width: 100%;
      height: 100px;
      border-radius: 8px;
      background-position: center;
      cursor: pointer;
    }
  }
}
</style>