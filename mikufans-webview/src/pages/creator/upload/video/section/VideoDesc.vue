<template>
  <div class="video-desc">
    <rich-input
      ref="richRef"
      class="h-[80px] m-1"
      v-model="value" 
      :submittable="false" 
      placeholder="请输入简介内容..."
      :disabled="formInstance.disabled"
    />
    <div class="flex px-2">
      <el-popover
        placement="top-start"
        trigger="click"
        width="310px"
        :disabled="formInstance.disabled"
        :popper-style="{padding:0}"
      >
        <template #reference>
          <i class="iconfont icon-biaoqing1 cursor-pointer grey1 text-[22px]" title="表情"></i>
        </template>
        <emoji-selector :input-ref="richRef"/>
      </el-popover>
      <span class="grey2 ml-auto">{{ richRef?.length + '/2048' }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import RichInput from '@/components/RichInput/index.vue';
import EmojiSelector from '@/components/EmojiSelector/index.vue';
import { formContextKey } from 'element-plus';
const formInstance = inject(formContextKey)
const props = defineProps<{ modelValue: string }>()
const emits = defineEmits<{
  'update:modelValue': [value: string]
}>()
const richRef = ref<InstanceType<typeof RichInput>>(null)
const value = ref(props.modelValue)
watchEffect(()=>emits('update:modelValue', value.value))
watchEffect(()=>value.value=props.modelValue)
</script>

<style scoped lang="scss">
.video-desc {
  flex: 1 0 0;
  padding: 4px;
  border-radius: 4px;
  line-height: 1.25;
  border: 1px solid var(--grey2);
  &:focus-within {
    border-color: var(--blue1);
  }
}
</style>