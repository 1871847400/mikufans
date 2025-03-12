<template>
  <div>
    <div class="flex items-center mb-4">
      <span class="text-xl font-bold mr-6">正片</span>
      <simple-tab 
        type="text"
        @update:index="page=$event"
        :options="groups" 
        style="font-size: 13px; gap: 20px;"
      />
    </div>
    <div class="part-list">
      <template v-for="i in partList" :key="i.id">
          <a class="part-item" :href="i.uri" target="_blank" :history="lastPlay===i.id">
          <div class="banner">
            <miku-image :res-id="i.bannerId" />
          </div>
          <div class="detail">
            <div class="leading-7">第{{ i.sort }}集</div>
            <div class="grey1 text-xs empty:hidden">{{ i.partName }}</div>
          </div>
          </a>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
const { video } = toRefs(useBangumiStore())
//上次观看的
const lastPlay = computed(()=>{
  return video.value?.history?.partId
})
const page = ref(0)
const pageSize = 8
const partList = computed(()=>{
  const start = page.value * pageSize
  return video.value.parts.slice(start, start + pageSize)
})
const groups = computed(()=>{
  const result = []
  for (let i = 1; i <= video.value.canplayCount; i+=pageSize) {
    result.push(`第${i}集 - 第${i+pageSize-1}集`)
  }
  return result
})
</script>

<style scoped lang="scss">
.part-list {
  margin-top: 24px;
  min-height: 150px;
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(4, 1fr);
}
.part-item {
  cursor: pointer;
  display: flex;
  border-radius: 6px;
  overflow: hidden;
  height: fit-content;
  border: .5px solid var(--bg2);
  transition: all .3s;
  &:hover * {
    color: var(--blue0);
  }
  &[history=true] .banner {
    position: relative;
    &::after {
      content: '上次观看';
      position: absolute;
      inset: 0;
      color: #fff;
      background: rgba(0,0,0,.3);
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }
  .miku-image {
    width: 100px;
    height: auto;
    aspect-ratio: 16/9;
    pointer-events: none;
  }
  .detail {
    flex: 1;
    /* background-color: #f1f2f3; */
    background-color: var(--bg1);
    padding: 8px;
    div {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}
</style>