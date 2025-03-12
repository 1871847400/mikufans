import { copyText } from "@/utils/copy";
import ContextMenu from "@imengyu/vue3-context-menu";

export function showContextMenu(e: MouseEvent) {
  const { playerElement } = useVideoStore()
  e.preventDefault()
  ContextMenu.showContextMenu({
    x: e.x,
    y: e.y,
    theme: 'dark',
    getContainer() {
      //防止全屏下不显示menu,改变出现的容器
      if (document.fullscreenElement == playerElement) {
        return playerElement
      }
      return null //默认容器为body
    },
    items: [
      {
        label: '复制视频地址',
        onClick() {
          copyText(location.href)
        },
      }
    ]
  })
}