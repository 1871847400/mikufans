import { forEachChild } from "@/utils/dom"
import { clamp, clone, min } from "lodash"
import { MaybeRef } from "vue"

/**
 * 用于实现照片的瀑布流布局
 * @param _container 
 * @param options 
 * @returns 
 */
export function useWaterfall(_container: MaybeRef<HTMLElement>, options: {
  columns: MaybeRef<number>, //总列数
  gap: MaybeRef<number>, //间距
}) {
  function setCss() {
    const container = unref(_container)
    container.style.position = 'relative'
    container.style.boxSizing = 'content-box'
    forEachChild(container, child=>{
      child.style.position = 'absolute'
      child.style.boxSizing = 'border-box'
    })
  }

  const updatePositions = useDebounceFn(()=>{
    setCss()
    const container = unref(_container)
    const columns = unref(options.columns)
    const gap = unref(options.gap)
    const colWidth = (container.clientWidth - gap * columns + gap) / columns
    //代表各列的当前位置
    const tops = new Array(columns)
    //将数组每一项填充0
    tops.fill(0) 
    let childIndex = 0
    forEachChild(container, child=>{
      childIndex++
      //根据元素的宽高比计算元素应该占据多少列
      const aspect = clamp(Math.round(child.clientWidth / child.clientHeight), 1, columns)
      //把超出边界的位置排除 
      let top = Math.min(...tops.slice(0, tops.length - aspect + 1)) 
      //找到最低的位置
      const index = tops.indexOf(top)
      for (let i = 1; i < aspect; i++) {
        if (tops[index + i] > top) {
          top = tops[index + i]
        }
      }
      for (let i = 0; i < aspect; i++) {
        tops[index + i] = top + colWidth * aspect / child.clientWidth * child.clientHeight + gap
      }
      // console.log(childIndex, ...tops);
      
      const left = index * gap + index * colWidth
      child.style.width = colWidth * aspect + 'px'
      child.style.height = 'fit-content'
      child.style.top = top + 'px';
      child.style.left = left + 'px'
    })
    //设置容器高度,防止塌陷
    container.style.height = Math.max(...tops) + 'px'
  }, 250)
  useResizeObserver(document.documentElement,  updatePositions)
  return { updatePositions }
}