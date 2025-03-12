<template>
  <el-popover
    placement="bottom"
    :width="width"
    :visible="visible"
    :offset="4"
  >
    <template #reference>
      <el-input 
        ref="inputRef" 
        v-model="model" 
        @focus="visible=true" 
        @blur="visible=false" 
        clearable
        :placeholder="data.placeholder"
        :disabled="disabled"
        :readonly="readonly"
      >
        <template #prefix>
          <i v-if="selectIcon" class="iconfont" v-html="'&#x'+selectIcon.unicode"></i>
        </template>
      </el-input>
    </template>
    <el-scrollbar max-height="200px">
      <div class="grid grid-cols-3 gap-y-2 p-1">
        <template v-for="{ unicode, name, font_class } in iconList">
          <div class="icon-item" @click="model='icon-'+font_class" :title="name">
            <i class="iconfont mr-1" v-html="'&#x' + unicode"></i>
            <span>icon-{{ font_class }}</span>
          </div>
        </template>
      </div>
    </el-scrollbar>
  </el-popover>
</template>

<script setup lang="ts">
import { glyphs } from '@/assets/iconfont/iconfont.json'
defineProps<{ data: FormItem, disabled: boolean, readonly: boolean }>()
const model = defineModel<string>('modelValue')
const visible = ref(false)
const inputRef = useTemplateRef('inputRef')
const width = ref(0)
const iconList = computed(()=>{
  return glyphs.filter(a=>a.name.includes(model.value) || a.font_class.includes(model.value))
})
const selectIcon = computed(()=>{
  return glyphs.find(a=>a.font_class==model.value?.replace('icon-', ''))
})
onMounted(()=>{
  width.value = inputRef.value.$el.clientWidth
})
</script>

<style scoped lang="scss">
.icon-item {
  user-select: none;
  cursor: pointer;
  transition: background .3s;
  /* width: fit-content; */
  padding: 2px 4px;
  border-radius: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  &:hover {
    background-color: var(--bg1);
  }
}
</style>