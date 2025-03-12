<template>
  <div class="channel-select">
    <el-cascader
      v-model="model"
      style="width: fit-content"
      :options="channels"
      separator=" → "
      show-all-levels 
      popper-class="channel-select-cascader"
      :props="{label:'channelName',value:'id'}"
      placeholder="请选择分区"
    >
    <template v-slot="{node, data}">
      <span>{{ data.channelName }}</span>
      <span v-if="data.channelDesc" class="grey2 ml-2 text-xs">{{ data.channelDesc }}</span>
    </template>
  </el-cascader>
  </div>
</template>

<script setup>
import { listVideoChannels } from '@/apis/video/channel';
const { state: channels } = useAsyncState(listVideoChannels({
  child: true
}), [])
const model = defineModel({ 
  default: '',
  get(val) {
    return val
  },
  set(val) {
    if (Array.isArray(val)) {
      if (val.length) {
        return val[val.length-1]
      } else {
        return ''
      }
    }
    return val
  }
})
</script>

<style scoped lang="scss">

</style>

<style>
/* 如果是el-popover的内容:deep不生效 */
.channel-select-cascader li.el-cascader-node {
  height: 45px;
  line-height: 45px;
}
</style>