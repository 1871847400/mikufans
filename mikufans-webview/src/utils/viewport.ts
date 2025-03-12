import { ref } from "vue"

//视口的宽高
const vw = ref(document.documentElement.clientWidth)
const vh = ref(document.documentElement.clientHeight)
window.addEventListener('resize', (e)=>{
  vw.value = document.documentElement.clientWidth
  vh.value = document.documentElement.clientHeight
})
export function useViewport() {
  return {
    vw, vh
  }
}