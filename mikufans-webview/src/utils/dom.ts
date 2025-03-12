import { MaybeRef } from "vue"

/**
 * 一级一级往上找，直到遇到指定的class
 */
export function getParentByClass(element: object, ...classList: string[]) {
  do {
    if (element && element instanceof HTMLElement) {
      for (let className of classList) {
        if (element.classList.contains(className)) {
          return element
        }
      }
      element = element.parentElement
    } else {
      break
    }
  } while (element['parentElement'])
}

/**
 * 是否为子元素(或者孙子元素)
 */
export function isChildElement(child: Element, parent: Element) {
  let ele = child
  do {
    ele = ele?.parentElement
    if (ele === parent) {
      return true
    }
  } while(ele)
  return false
}
export function isChildNode(child: Node, parent: Node) {
  let ele = child
  do {
    ele = ele?.parentNode
    if (ele === parent) {
      return true
    }
  } while(ele)
  return false
}

export function forEachChild(container: MaybeRef<HTMLElement>, callback: (child: HTMLElement)=>void, deep = false) {
  const children = unref(container).children
  for (let i = 0; i < children.length; i++) {
    const child = children.item(i)
    if (child instanceof HTMLElement) {
      callback(child)
      if (deep) {
        forEachChild(child, callback, deep)
      }
    }
  }
}