export {}
declare module 'vue-router' {
  //注意children的meta会继承父路由
  interface RouteMeta {
    login?: boolean
    title?: string
  }
}