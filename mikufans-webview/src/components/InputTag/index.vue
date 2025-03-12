<!-- 输入框内输入文字,回车后添加标签,可以添加多个,删除键可以删除一个标签 -->
<template>
  <div class="input-tag" :class="{ disabled: formInstance.disabled }">
    <transition-group name="tag-list">
      <template v-for="i in modelValue" :key="i">
        <div class="tag" title="删除此标签" @click="deleteTag(i)">
          <span>{{ i }}</span>
          <i class="iconfont icon-guanbi"></i>
        </div>
      </template>
    </transition-group>
    <input :disabled="formInstance.disabled" class="flex-1" v-model.trim="value" maxlength="16" type="text" placeholder="按回车键增加标签" @keydown="keydown">
    <span v-if="max" class="grey2">还可以添加{{ max - modelValue.length }}个标签</span>
  </div>
</template>

<script setup lang="ts">
import { formContextKey } from 'element-plus';
const formInstance = inject(formContextKey)
const props = defineProps<{
  modelValue: string[]
  max?: number
}>()
const emits = defineEmits<{
  'update:modelValue': [data: string[]]
}>()
const value = ref('')
function keydown(e: KeyboardEvent) {
  const data = [...props.modelValue]
  if (e.key === 'Backspace') {
    if (value.value == '' && data.length) {
      data.splice(data.length - 1)
    }
  } else if (e.key === 'Enter') {
    const content = value.value.trim()
    if (content == '') return
    if (props.max && data.length >= props.max) {
      return message.warning('标签数量达到限制')
    }
    if (data.includes(content)) {
      return message.warning('标签已存在')
    }
    data.push(content)
    value.value = ''
  }
  emits('update:modelValue', data)
}
function deleteTag(name: string) {
  emits('update:modelValue', props.modelValue.filter(tag=>tag!=name))
}
</script>

<style scoped lang="scss">
.input-tag {
  line-height: 32px;
  padding: 4px 8px;
  border-radius: 4px;
  overflow: hidden;
  display: flex;
  align-items: center;
  gap: 4px;
  flex-wrap: wrap;
  border: 1px solid var(--grey2);
  &:focus-within {
    border-color: var(--blue1);
  }
  input {
    background: none;
  }
  input::placeholder {
    color: var(--grey2);
  }
  &.disabled {
    pointer-events: none;
  }
  .tag {
    background-color: var(--blue1);
    border-radius: 6px;
    color: #fff;
    cursor: pointer;
    user-select: none;
    font-size: 14px;
    height: 20px;
    line-height: 20px;
    white-space: nowrap;
    padding: 4px 8px;
    .iconfont {
      padding-left: 4px;
      font-size: 12px;
    }
  }
  .tag-list-enter-active, .tag-list-leave-active {
    transition: transform 0.3s ease;
  }
  .tag-list-enter-from {
    transform: scale(1.1);
  }
  .tag-list-leave-to {
    transform: scale(0.1);
  }
}
</style>