import { Component, createApp } from "vue"
import { DialogProps, ElImageViewer } from 'element-plus';
import { renderComponent } from './vue';
import { render } from 'vue';
import type { ComponentProps } from 'vue-component-type-helpers'

/**
 * 打开自带的文件选择框
 */
export function openFileSelect(params: {
  accept?: string, //过滤的媒体类型如: image/png video/*
  multiple?: boolean, //是否可以选择多个文件
  directory?: boolean, //是否为选择文件夹
  callback?: (file: File)=>void, //对每个文件执行回调
  callbacks?: (files: File[])=>void, //无论多少个文件只执行一次
}) {
  return new Promise<File[]>((resolve)=>{
    const input = document.createElement('input')
    input.type = 'file'
    input.accept = params.accept ?? '*/*'
    input.hidden = true
    input.multiple = !!params.multiple
    input.webkitdirectory = !!params.directory
    input.addEventListener('change', e=>{
      const files = Array.from(input.files)
      if (files.length > 0) {
        files.forEach(file=>params.callback?.call(this, file))
        params.callbacks?.call(this, files)
      }
      input.remove()
      resolve(files)
    })
    input.click()
  })
}
/**
 * 动态打开ElDialog,要求打开的组件的根标签为el-dialog
 */
export function openDialog<P extends Component>(component: P, props?: ComponentProps<P> & Partial<DialogProps>) {
  return new Promise<void>((resolve)=>{
    const { vnode, container } = renderComponent(component, {
      lockScroll: false, //防止滚动条造成抖动
      // closeOnClickModal: false, //防止点击模态来关闭dialog
      destroyOnClose: true, //清除dialog的内容,但会保留外层的el-dialog
      ...props,
      //onXXX 会绑定到根组件的事件上
      //例如onClosed用于绑定到el-dialog的closed事件
      onClosed(...params) {
        if (typeof props?.onClosed === 'function') {
          props.onClosed(...params)
        }
        render(null, container)
        container.remove()
        resolve()
      },
    })
  })
}

/**
 * ELImage的图片预览
 * 创建新APP实例来渲染组件
 */
export function openImagePreview(params: {
  urlList: string[],
  initialIndex?: number
}) {
  const box = document.createElement('div')
  document.body.appendChild(box)
  //注意该app没有像主app一样注册插件,指令
  const app = createApp(ElImageViewer, {
    ...params,
    onClose() {
      app.unmount()
      box.remove()
    },
  })
  app.mount(box)
}


// /**
//  * 创建一个新的APP实例来打开dialog,关闭时销毁
//  */
// export function openVueDialog<Props>(component: Component<Props>, props?: (Props & VNodeProps)) {
//   return new Promise<void>((resolve)=>{
//     const box = document.createElement('div')
//     document.body.appendChild(box)
//     // rootProps: defineProps中定义的以外的属性会透传给根组件,比如事件
//     const app = createApp(component, {
//       'lock-scroll': false, //防止滚动条造成抖动
//       'close-on-click-modal': false, //防止点击模态来关闭dialog
//       ...props,
//       // onXXX 会绑定到根组件的事件上
//       // 例如onClosed用于绑定到el-dialog的closed事件
//       onClosed(...params: any[]) {
//         const func = props?.['onClosed']
//         if (typeof func === 'function') {
//           func(...params)
//         }
//         app.unmount()
//         box.remove()
//         resolve()
//       },
//     })
//     //新APP实例,需要和主APP一样注册插件,指令等
//     initApp(app)
//     //挂载到新添加的元素
//     app.mount(box)
//   })
// }