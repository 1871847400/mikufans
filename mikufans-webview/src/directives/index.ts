import { App } from "vue";
import visible from './visible'
import mutation from "./mutation";
import focus from "./focus";
import resize from "./resize";

export function installDirectives(app: App) {
  //自动获取焦点,只支持表单元素
  app.directive('focus', focus)
  //监听元素大小变化
  app.directive('resize', resize)
  //监听元素内容变化
  app.directive('mutation', mutation)
  //控制元素的显示与隐藏
  app.directive('visible', visible)
}