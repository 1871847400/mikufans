<!-- <template>
  <slot></slot>
  <ul v-if="reference" class="menu" ref="floating" :style="floatingStyles">
    <template v-for="i in options.filter(a=>!a.hide)">
      <li @click.prevent="onClick($event, i)">{{ i.label }}</li>
    </template>
  </ul>
</template>

<script setup lang="ts">
import { autoPlacement, offset, useFloating } from '@floating-ui/vue';
export interface ContextMenuOption {
  label: string
  hide?: boolean
  click: (params: any, e: MouseEvent)=>void,
}
const props = defineProps({
  options: array<ContextMenuOption>().def([]), 
})
const floating = ref<HTMLElement>()
//虚拟元素
const reference = ref({
  getBoundingClientRect() {
    return {
      width: 0,
      height: 0,
      x: 0,
      y: 0,
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
    }
  }
})
//reference的真实元素
const el = ref<HTMLElement>()
const middleware = ref([
  autoPlacement({
  }),
])
const { floatingStyles } = useFloating(reference, floating, {
  middleware,
  strategy: 'fixed',
});
onMounted(()=>{
  el.value = floating.value?.previousElementSibling as HTMLElement
  //让menu的位置出现在鼠标右下角
  middleware.value.push(offset({
    // mainAxis: floating.value.clientWidth,
    crossAxis: floating.value.clientHeight/2
  }))
  reference.value = null
})
useEventListener(el, 'contextmenu', e=>{
  e.preventDefault()
  handleClick(e)
})
useEventListener('click', ()=>{
  reference.value = null
})
//保持只显示一个contextmenu
useEventListener('contextmenu', e=>{
  const rect = reference.value?.getBoundingClientRect()
  if (rect) {
    if (e.clientX !== rect?.x || e.clientY !== rect?.y) {
      reference.value = null
    }
  }
})
function handleClick({clientX, clientY}) {
  reference.value = {
    getBoundingClientRect() {
      return {
        width: 0,
        height: 0,
        x: clientX,
        y: clientY,
        top: clientY,
        left: clientX,
        right: clientX,
        bottom: clientY,
      };
    },
  };
}
function onClick(e: MouseEvent, option: ContextMenuOption) {
  reference.value = null
  option.click(props.params, e)
}
</script>

<style scoped lang="scss">
ul.menu {
  border-radius: 6px;
  padding: 4px 0;
  min-width: 90px;
  overflow: hidden;
  background-color: var(--bg1);
  z-index: 1000;
  li {
    line-height: 30px;
    user-select: none;
    cursor: pointer;
    text-align: center;
    transition: all .2s;
    &:hover {
      background-color: var(--bg2);
    }
  }
}
</style> -->