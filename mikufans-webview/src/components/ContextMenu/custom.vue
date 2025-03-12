<!-- <template>
  <div class="context-menu" ref="contextMenuRef">
    <slot></slot>
    <!-- 将菜单移动到body下,防止相对的元素出错 -->
    <teleport to='body'>
      <!-- 高度auto不支持过渡动画,必须为精确的值,所以需要用js控制 -->
      <transition
        @before-enter="beforeEnter"
        @enter="enter"
        @after-enter="afterEnter"
      >
        <ul class="context-menu-popover"
          v-resize="sizeChanged"
          v-if="showMenu"
          :style="{
            left: pos.x + 'px',
            top: pos.y + 'px'
          }"
        >
          <li class="context-menu-item" 
            v-for="i in menus" 
            @click.prevent.stop="()=>{i?.onClick();emits('select',i)}" 
            v-show="!!!i.hide">{{ i.label }}</li>
        </ul>
      </transition>
    </teleport>
  </div>
</template>

<script setup lang="ts">
import { useContextMenu } from '@/hooks/contextmenu';
import { useViewport } from '@/utils/viewport';
export interface ContextMenuItemOptions {
  label: string
  hide?: boolean //是否隐藏
  onClick?: ()=>void
}
const props = defineProps<{
  menus: ContextMenuItemOptions[],
}>()
const emits = defineEmits<{
  select: [option: ContextMenuItemOptions]
}>()
const contextMenuRef = ref<HTMLElement>(null)
//鼠标位置,是否显示菜单
const { x: mouseX, y: mouseY, showMenu } = useContextMenu(contextMenuRef)
//获取视口宽高(documentElement)
const { vw, vh } = useViewport()
function beforeEnter(el: HTMLElement) {
  el.style.height = '0'
}
function enter(el: HTMLElement) {
  //将高度设置为auto,让浏览器计算
  el.style.height = 'auto'
  //计算后的结果
  const h = el.clientHeight
  el.style.height = '0'
  //当前帧
  requestAnimationFrame(()=>{
    //下一帧
    requestAnimationFrame(()=>{
      el.style.transition = '.2s'
      el.style.height = h + 'px'
    })
  })
}
function afterEnter(el: HTMLElement) {
  el.style.transition = 'none'
}
const width = ref(0)
const height = ref(0)
function sizeChanged(e) {
  width.value = e.width
  height.value = e.height
}
//动态确定menu的位置
//防止菜单显示到点不到的位置
const pos = computed(()=>{
  let [x, y] = [mouseX.value, mouseY.value]
  if (y + height.value > vh.value) {
    y = vh.value - height.value
  }
  if (x + width.value > vw.value) {
    x = vw.value - width.value
  }
  return {
    x, y
  }
})
</script>

<style scoped lang="scss">
.context-menu {
  width: fit-content;
}
.context-menu-popover {
  box-sizing: border-box;
  position: fixed; //相对于window
  background-color: var(--bg0);
  border-radius: 6px;
  min-width: 100px;
  padding: 4px 0;
  height: 0;
  overflow: hidden;
  box-shadow: 0 0 1px var(--bg0);
  z-index: 9999999;
}
html.dark {
  .context-menu-item:hover {
    background-color: #444;
  }
}
.context-menu-item {
  box-sizing: border-box;
  user-select: none;
  cursor: pointer;
  text-align: center;
  height: 24px;
  line-height: 24px;
  padding: 0 8px;
  color: var(--grey2);
  &:hover {
    background-color: #ccc;
  } 
}
</style> -->