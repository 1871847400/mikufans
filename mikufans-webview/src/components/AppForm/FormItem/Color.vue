<template>
   <div class="flex gap-2">
      <el-color-picker ref="picker" v-model="modelValue" />
      <el-popover
        :visible="visible"
        placement="bottom" 
        width="auto"
      >
        <div class="flex gap-2 p-2">
          <div v-for="color in colors" @click="modelValue=color" class="color-piece" :style="`--color: ${color}`"></div>
        </div>
        <template #reference>
          <el-input v-model="modelValue" :placeholder="data.placeholder" class="w-[180px]">
             <template #suffix>
                <i class="iconfont icon-xiugai straw" title="吸管" @click="straw"></i>
             </template>
          </el-input>
        </template>
      </el-popover>
   </div>
</template>

<script setup lang="ts">
import { Vibrant } from "node-vibrant/browser";
defineProps<{ data: FormItem }>()
const visible = ref(true)
const modelValue = defineModel<string>()
const colors = ref([])
async function handler(e: MouseEvent) {
  unlink()
  e.preventDefault()
  e.stopPropagation()
  if (e.target instanceof HTMLImageElement) {
    colors.value = []
    const palette = await Vibrant.from(e.target).getPalette()
    for (const style in palette) {
      colors.value.push(palette[style].hex)
    }
    visible.value = true
  }
}
useEventListener('click', ()=>{
  visible.value = false
})
function straw() {
  setTimeout(() => {
    message.info('请点击一张图片,以吸取颜色。')
    addEventListener('click', handler, { capture: true, once: true })
    document.documentElement.style.cursor = 'grab'
  }, 0)
}
function unlink() {
  removeEventListener('click', handler)
  document.documentElement.style.cursor = ''
}
onBeforeUnmount(unlink)
</script>

<style scoped lang="scss">
.straw {
  cursor: pointer;
  user-select: none;
}
.color-piece {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  cursor: pointer;
  background-color: var(--color);
}
</style>