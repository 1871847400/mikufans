<template>
  <div class="mb-4">
    <div class="h-10 flex justify-between items-center bg1 rounded-lg px-4">
      <span>弹幕列表</span>
      <miku-arrow v-model="expand"/>
    </div>
    <height-transition :expand="expand">
      <div class="danmu-list-box">
        <div class="head">
          <span v-for="{ label, sort, desc },index in heads" @click="setSort(index)">
            <span>{{ label }}</span>
            <i v-if="sort" class="iconfont icon-jiantoushang text-xs mr-2" :style="{rotate:desc?'180deg':'0deg'}"></i>
          </span>
        </div>
        <el-scrollbar max-height="300px">
          <ul>
            <li class="list-item" v-for="i in danmuList" :key="i.id">
              <span>{{ displayDuration(i.sendTime*1000) }}</span>
              <span :title="i.content">{{ i.content }}</span>
              <span>{{ i.createDate }}</span>
            </li>
          </ul>
        </el-scrollbar>
      </div>
    </height-transition>
  </div>
</template>

<script setup lang="ts">
import { displayDuration } from '@/utils/common';
import { sortBy, uniqBy } from 'lodash';
const { danmus } = toRefs(useVideoStore())
const expand = ref(false)
const heads = reactive([
  { label: '时间', value: 'sendTime', sort: false, desc: false },
  { label: '内容', value: 'content', sort: false, desc: false },
  { label: '日期', value: 'createTime', sort: false, desc: false },
])
function setSort(i: number) {
  heads.forEach(a=>{
    a.sort = false
  })
  heads[i].sort = true
  heads[i].desc = !heads[i].desc
}
const danmuList = computed(()=>{
  let result = uniqBy(danmus.value, 'id')
  const sort = heads.find(a=>a.sort)
  if (sort) {
    result = sortBy(danmus.value, sort.value)
    if (sort.desc) {
      result = result.toReversed()
    }
  }
  return result
})
</script>

<style scoped lang="scss">
.danmu-list-box {
  padding: 10px 4px;
  .head {
    display: flex;
    padding-bottom: 4px;
    span {
      cursor: pointer;
      user-select: none;
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 14px;
      color: var(--grey1);
    }
    span:nth-child(1) {
      width: 70px;
    }
    span:nth-child(2) {
      flex: 1;
    }
    span:nth-child(3) {
      width: 90px;
    }
  }
  .list-item {
    display: flex;
    padding: 4px 0;
    overflow: hidden;
    span:nth-child(1) {
      width: 70px;
      color: var(--grey2);
      font-size: 12px;
    }
    span:nth-child(2) {
      flex: 1;
      font-size: 14px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      width: 0;
      padding-right: 4px;
    }
    span:nth-child(3) {
      width: 90px;
      color: var(--grey2);
      font-size: 12px;
    }
  }
}
</style>