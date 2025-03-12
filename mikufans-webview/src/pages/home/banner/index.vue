<template>
  <div ref="container" class="banner">
    <img src="@/assets/images/home_banner/1.png" alt="">
    <img src="@/assets/images/home_banner/2.png" alt="">
    <img src="@/assets/images/home_banner/3.png" alt="">
    <img src="@/assets/images/home_banner/4.png" alt="">
    <img src="@/assets/images/home_banner/5.png" alt="">
    <img src="@/assets/images/home_banner/6.png" alt="">
    <img src="@/assets/images/home_banner/7.png" alt="">
    <img src="@/assets/images/home_banner/8.png" alt="">
    <img src="@/assets/images/home_banner/9.png" alt="">
    <img src="@/assets/images/home_banner/10.png" alt="">
    <img src="@/assets/images/home_banner/11.png" alt="">
    <img src="@/assets/images/home_banner/12.png" alt="">
    <img src="@/assets/images/home_banner/13.png" alt="">
    <img src="@/assets/images/home_banner/14.png" alt="">
    <img src="@/assets/images/home_banner/15.png" alt="" class="hidden">
    <img src="@/assets/images/home_banner/16.png" alt="">
    <img src="@/assets/images/home_banner/17.png" alt="">
    <img src="@/assets/images/home_banner/18.png" alt="">
  </div>
</template>

<script setup lang="ts">
import gsap from 'gsap'
const container = ref<HTMLDivElement>(null)
function childAt(index: number) {
  return container.value.children.item(index) as HTMLImageElement
}
let enter = false
const children : HTMLElement[] = []
function mouseenter() {
  enter = true
}
function mousemove(e: MouseEvent) {
  if (!enter) return
  gsap.to(childAt(11), {
    translateX: `+=${e.movementX}px`,
    duration: 1.2
  })
  gsap.to(childAt(13), {
    translateX: `+=${e.movementX}px`,
    duration: 0.6
  })
  gsap.to(childAt(7), {
    translateX: `+=${e.movementX}px`,
    duration: 2
  })
  gsap.to(childAt(4), {
    translateX: `+=${-e.movementX}px`,
    duration: 2
  })
  gsap.to(childAt(5), {
    translateX: `+=${e.movementX}px`,
    duration: 0.8
  })
  gsap.to(childAt(12), {
    translateX: `+=${e.movementX}px`,
    duration: 1.2
  })
  //水滴
  gsap.to(childAt(15), {
    translateX: `+=${e.movementX*2}px`,
    translateY: `+=${e.movementX}px`,
    duration: 1.5
  })
  gsap.to(childAt(16), {
    translateX: `+=${e.movementX}px`,
    translateY: `+=${e.movementX}px`,
    duration: 1
  })
  gsap.to(childAt(10), {
    translateX: `+=${e.movementX*0.5}px`,
    translateY: `+=${e.movementX}px`,
    duration: 1.2
  })
  gsap.to(childAt(9), {
    translateX: `+=${e.movementX*0.5}px`,
    translateY: `+=${e.movementX}px`,
    duration: 1.2
  })
}
function mouseleave(e: MouseEvent) {
  enter = false
  if (children.length === 0) {
    Array.from(container.value.children)
      .forEach(e=>children.push(e as HTMLElement))
  }
  for (const child of children) {
    for (const tween of gsap.getTweensOf(child)) {
      tween.kill()
    }
    gsap.to(child, {
      translateX: 0,
      translateY: 0,
      duration: 0.2
    })
  }
}
onMounted(()=>{
  //navbar阻挡了鼠标事件,需要父元素来充当事件触发
  const parent = container.value.parentElement
  parent.addEventListener('mouseenter', mouseenter)
  parent.addEventListener('mousemove', mousemove, { passive: true })
  parent.addEventListener('mouseleave', mouseleave)
})
onBeforeUnmount(()=>{
  const parent = container.value.parentElement
  parent.removeEventListener('mouseenter', mouseenter)
  parent.removeEventListener('mousemove', mousemove)
  parent.removeEventListener('mouseleave', mouseleave)
})
</script>

<style scoped lang="scss">
.banner {
  pointer-events: none;
  position: relative;
  width: 100%;
  height: 155px;
  overflow: hidden;
  img {
    /* width: auto; */
    width: 100%;
    height: 100%;
    position: absolute;
    /* left: -50px; */
    left: 0;
    top: 0;
    object-fit: cover;
  }
  html.dark & {
    filter: brightness(.75);
  }
}
</style>