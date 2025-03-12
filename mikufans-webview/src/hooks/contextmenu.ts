/**
 * 根据鼠标位置确定菜单应该出现的位置
 */
export function useContextMenu(container: HTMLElement | Ref<HTMLElement>) {
  const x = ref(0)
  const y = ref(0)
  const showMenu = ref(false)
  function openMenu(e: MouseEvent) {
    e.preventDefault()
    //阻止冒泡,防止上级元素打开菜单
    e.stopPropagation()
    //鼠标相对于页面的位置
    x.value = e.clientX
    y.value = e.clientY
    showMenu.value = true
  }
  function closeMenu() {
    showMenu.value = false
  }
  onMounted(()=>{
    if (container instanceof HTMLElement) {
      container.addEventListener('contextmenu', openMenu)
    } else {
      container.value.addEventListener('contextmenu', openMenu)
    }
    //打开其它菜单前应该关闭之前的菜单
    //需要在事件捕获阶段关闭菜单
    window.addEventListener('click', closeMenu, true)
    window.addEventListener('contextmenu', closeMenu, true)
  })
  onUnmounted(()=>{
    if (container instanceof HTMLElement) {
      container.removeEventListener('contextmenu', openMenu)
    } else {
      container.value?.removeEventListener('contextmenu', openMenu)
    }
    window.removeEventListener('click', closeMenu)
    window.removeEventListener('contextmenu', closeMenu)
  })
  return { x, y, showMenu }
}