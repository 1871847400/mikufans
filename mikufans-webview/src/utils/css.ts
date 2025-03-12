/**
 * 当前元素或父级之上是否包含指定class
 */
export function hasClassParent(element: object, ...classList: string[]) {
  do {
    if (element && element instanceof HTMLElement) {
      for (let className of classList) {
        if (element.classList.contains(className)) {
          return true
        }
      }
      element = element.parentElement
    } else {
      break
    }
  } while (element && element['parentElement'])
  return false
}