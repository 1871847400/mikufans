import app from "@/main";
import { Component, render, VNode, VNodeProps } from "vue";

export function renderComponent<Props>(component: Component<Props>, props?: (Props & VNodeProps)) {
  const vnode = h(component, props)
  //赋予节点上下文,否则无法用store之类的
  vnode.appContext = app._context
  //创建一个容器来渲染vnode,相当于#app
  const container = document.createElement('div')
  document.body.append(container)
  //渲染内容到容器,不会改变容器本身的属性
  //如果要卸载vnode: render(null, 容器)
  render(vnode, container)
  return { container, vnode }
}