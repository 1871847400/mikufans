<template>
  <div class="p-3 text-sm">
    <div>按类型屏蔽</div>
    <div class="flex items-center gap-4">
      <label v-for="{ label, icon, icon2, value } in banTypes" :key="value" :class="{active:danmuSetting.ban[value]}">
        <i class="iconfont" v-html="danmuSetting.ban[value] ? icon2 : icon"></i>
        <span>{{ label }}</span>
        <input type="checkbox" v-model="danmuSetting.ban[value]" hidden>
      </label>
    </div>
    <div class="flex items-center gap-2 mt-2">
      <el-checkbox v-model="danmuSetting.density" true-value="pretty" false-value="overlap" label="防止弹幕重叠" />
      <el-checkbox v-model="danmuSetting.syncVideo"  label="同步视频速度" />
    </div>
    <div class="flex flex-col gap-2 py-2">
      <div class="progress-item">
        <span>不透明度</span>
        <el-slider v-model="danmuSetting.opacity" :min="0.1" :max="1" :step="0.01" size="small" :show-tooltip="false" />
        <span>{{ danmuSetting.opacity*100|0 }}%</span>
      </div>
      <div class="progress-item">
        <span>显示区域</span>
        <el-slider v-model="danmuSetting.showArea" :min="0.2" :max="1" :step="0.2" size="small" :show-tooltip="false" show-stops />
        <span>{{ danmuSetting.showArea*100|0 }}%</span>
      </div>
      <div class="progress-item">
        <span>弹幕字号</span>
        <el-slider v-model="danmuSetting.fontSize" :min="10" :max="24" :step="1" size="small" :show-tooltip="false" />
        <span>{{ danmuSetting.fontSize/16*100|0 }}%</span>
      </div>
      <div class="progress-item">
        <span>弹幕速度</span>
        <el-slider v-model="duration" :min="minDuration" :max="maxDuration" :step="1000" size="small" :show-tooltip="false" />
        <span>{{ duration/6000*100|0 }}%</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { danmuSetting } from './config';
const banTypes = [
  { label: '滚动', value: 'roll',  icon: '&#xe69f;', icon2: '&#xe69e;' },
  { label: '固定', value: 'fixed', icon: '&#xe69b;', icon2: '&#xe69a;'  },
  { label: '彩色', value: 'color', icon: '&#xe70a;', icon2: '&#xe709;' },
]
const minDuration = 3000
const maxDuration = 12000
const duration = computed({
  get() {
    return (minDuration + maxDuration) - danmuSetting.value.duration
  },
  set(val) {
    danmuSetting.value.duration = (minDuration + maxDuration) - val
  }
})
</script>

<style scoped lang="scss">
label {
  display: flex;
  flex-direction: column;
  cursor: pointer;
  align-items: center;
  font-size: 14px;
  user-select: none;
  .iconfont {
    font-size: 40px;
    transform: translateX(2px);
    height: 42px;
  }
  &.active {
    color: var(--blue0);
  }
}
.el-checkbox {
  flex-direction: row;
}
.progress-item {
  display: flex;
  align-items: center;
  white-space: nowrap;
  gap: 8px;
  .el-slider {
    --el-slider-button-size: 10px;
    /* --el-slider-main-bg-color: #fff; */
    width: 150px;
  }
}
</style>