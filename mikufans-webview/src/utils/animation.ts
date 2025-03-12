import { gsap } from "gsap"

/**
 * 水花效果,主要用于一键三连
 */
export function splash(params: {
  container: HTMLElement, //在指定容器下产生
  offset: number,
  color: string, //水花颜色
  size: number,
  delay?: number //延迟(ms)
}) {
  for(let i = 0; i < 8; i++) {
    const parent = document.createElement('div')
    const div = document.createElement('div')
    parent.style.position = 'absolute'
    parent.style.inset = '0'
    parent.style.rotate = (params.offset + i * 45) + 'deg'
    parent.style.zIndex = '9999999'
    div.style.position = 'absolute'
    div.style.left = `calc(50% - ${params.size/2}px)`
    div.style.top = `calc(50% - ${params.size/2}px)`
    div.style.width = params.size + 'px'
    div.style.height = params.size + 'px'
    div.style.borderRadius = (params.size/2)+'px'
    div.style.background = params.color
    parent.appendChild(div)
    params.container.appendChild(parent)
    gsap.to(div, {
      translateY: '60px',
      scaleY: 2,
      duration: 1,
      delay: params.delay
    }).then((result)=>{
      parent.remove()
    })
  }
}