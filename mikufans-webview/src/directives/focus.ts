import { Directive } from "vue";

export default {
  mounted: (el: HTMLElement) => {
    setTimeout(() => {
      el.focus()
    }, 100);
  },
} as Directive