<template>
  <el-popover 
    trigger="hover" 
    placement="bottom" 
    @before-enter="lazyShow=true"
    :open-delay="200"
    :width="isLogin?width:'300px'"
    :popper-options="popoverOptions"
    :disabled="!component"
  >
    <template v-if="component">
      <component v-if="lazyShow && isLogin" :is="component"/>
      <div v-else-if="!isLogin" class="p-4">
        <div class="text-center py-4">登录后才能查看{{ label }}</div>
        <el-button class="w-full" type="primary" @click="$router.push('/login')">立即登录</el-button>
      </div>
    </template>
    <template #reference>
      <router-link class="active-item" @click="onClick" :to="path" target="_blank" :count="showCount" draggable="false">
        <use-window-size v-slot="{ width }">
          <svg-icon :name="icon" :size="width>=1200?20:24"/>
          <span v-if="width>=1200">{{ label }}</span>
        </use-window-size>
      </router-link>
    </template>
  </el-popover>
</template>

<script setup lang="ts">
import { UseWindowSize } from '@vueuse/components'
import { Options } from 'element-plus'
import { MaybeRef, MaybeRefOrGetter } from 'vue';
import { openDialog } from '@/utils/dialog';
import UserLoginDialog from '@/components/UserLoginDialog/index.vue';
const props = defineProps<{
  label: string
  icon: string
  path: string
  count: MaybeRef<number>
  width: string
  offset: [number, number]
  component?: Component
}>()
const { isLogin } = toRefs(useUserStore())
//延迟加载popover的内容
const lazyShow = ref(false)
const showCount = computed(()=>{
  const count = unref(props.count)
  return count > 99 ? '99+' : count
})
//popover v2.0 配置 
const popoverOptions : Partial<Options> = {
  modifiers: [
    {
      name: 'offset',
      options: { offset: props.offset }
    },
  ],
}
function onClick(e: Event) {
  if (!isLogin.value) {
    e.preventDefault()
    openDialog(UserLoginDialog, { mode: 0 })
  }
}
</script>

<style scoped lang="scss">
.active-item {
  white-space: nowrap; //防止窗口化后换行
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  font-size: 13px;
  cursor: pointer;
  margin-right: 20px;
  color: inherit;
  position: relative;
  &:hover .svg-icon {
    animation: jump 0.5s;
  }
  @keyframes jump {
    50% {
      transform: translateY(-5px);
    }
  }
  &[count='0']::after {
    display: none;
  }
  &::after {
    content: attr(count);
    font-size: 10px;
    color: #fff;
    width: 20px;
    padding: 1px 0;
    text-align: center;
    position: absolute;
    background-color: #fa5a57;
    overflow: hidden;
    line-height: 15px;
    border-radius: 50%;
    top: -4px;
    right: -10px;
  }
}
</style>