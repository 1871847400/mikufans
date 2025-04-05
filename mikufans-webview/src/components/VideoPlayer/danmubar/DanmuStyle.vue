<template>
  <div class="p-3 text-sm">
    <div>弹幕类型</div>
    <div class="flex items-center gap-4">
      <template v-for="{ label, value, icon } in types" :key="value" >
        <div class="danmu-type" :class="{active:danmuStyle.type==value}" @click="danmuStyle.type=value">
          <i class="iconfont" v-html="icon"></i>
          <span>{{ label }}</span>
        </div>
      </template>
    </div>
    <div class="mt-3 mb-1">弹幕颜色</div>
    <div>
      <div class="flex gap-1">
        <input class="color-input" v-model="danmuStyle.color" type="text" maxlength="7" placeholder="#FFF">
        <div class="color-palette" :style="{background:danmuStyle.color}"></div>
      </div>
      <ul class="color-piece">
        <template v-for="{ colorName, colorCode } in colors" :key="colorCode">
          <li 
            :class="{active:danmuStyle.color==colorCode}" 
            :style="{background:colorCode}" 
            :title="colorName" 
            @click="danmuStyle.color=colorCode"
          ></li>
        </template>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { danmuStyle } from './config';
import videoDanmuApi from '@/apis/video/danmu';
const types = [
  { label: '滚动', value: 'ROLL' as DanmuType, icon: '&#xe69f;' },
  { label: '顶部', value: 'FIXED_TOP' as DanmuType, icon: '&#xe69b;' },
  { label: '底部', value: 'FIXED_BOTTOM' as DanmuType, icon: '&#xe69d;' },
]
const { state: colors } = useAsyncState(videoDanmuApi.getColorList(), [])
watch(()=>danmuStyle.color, (color)=>{
  if (!color.startsWith('#')) {
    danmuStyle.color = '#' + color.replace('#', '')
  }
})
</script>

<style scoped lang="scss">
.danmu-type {
  cursor: pointer;
  user-select: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 12px;
  .iconfont {
    font-size: 36px;
    translate: 2px;
    height: 36px;
  }
  &.active {
    color: var(--blue0);
  }
}
.color-input {
  background: none;
  color: #fff;
  border: 1px solid #ccc;
  border-radius: 4px;
  height: 24px;
  text-indent: 8px;
  width: 100px;
  font-weight: 550;
}
.color-palette {
  border-radius: 4px;
  width: 60px;
  height: 24px;
}
.color-piece {
  padding: 10px 0;
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  row-gap: 4px;
  column-gap: 8px;
  li {
    width: 20px;
    aspect-ratio: 1;
    border-radius: 2px;
    border: 2px solid #ccc;
    cursor: pointer;
    &.active {
      border-color: #eee;
    }
  }
}
</style>