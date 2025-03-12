import { cloneDeep, groupBy, isNil, sortBy } from "lodash";
import { Router, RouteRecordRaw } from "vue-router";
/**
 * 利用 Vite glob 根据目录结构 自动生成路由
 * 必须静态指定路径才能使用glob
 * glob() 会返回: Record<文件路径, 导入函数>
 */
export function generate(router: Router) {
  const indexes = import.meta.glob('/src/pages/admin/**/route.ts', {
    eager: true, //value将不是函数而是Module
    import: 'default',
  })
  //按照路径长度升序
  const entries = Object.entries(indexes)
    .sort((a,b)=>a[0].length-b[0].length)
  const comIndexes = import.meta.glob('/src/pages/admin/**/index.vue')
  entries.forEach(([filepath, meta])=>{
    const path = filepath.replace('/src/pages/admin', '/admin')
      .replace('/route.ts', '') || '/'
    const name = path.split('/')
      .filter(Boolean)
      .join('-') || 'index'
    let parent : RouteRecordRaw = null
    const routes = router.getRoutes().filter(a=>a.path.startsWith('/admin'))
    for (const route of routes) {
      if (route.redirect) {
        continue
      }
      if (isChild(path, route.path)) {
        parent = route
        break
      }
    }
    const route : RouteRecordRaw = {
      path,
      name,
      component: comIndexes[filepath.replace('/route.ts', '/index.vue')],
      meta: meta as any,
      async beforeEnter(to, from, next) {
        await useSysUserStore().getUser()
        next()
      }
    }
    if (parent) {
      router.addRoute(parent.name, route)
    } else {
      router.addRoute(route)
    }
  })
}

export function isChild(childPath: string, parentPath: string) {
  // console.log(childPath, parentPath);
  return childPath.startsWith(parentPath) && 
    childPath.replace(parentPath, '')
    .lastIndexOf('/')===0
  || childPath.lastIndexOf('/') === 0 && parentPath === '/'
}

/**
 * 按照路径(route.path)生成路由的树形父子结构 并不等于实际的路由父子结构
 * 子项为深度克隆不会干涉原路由
 */
export function useRouteTree(basePath: string) {
  const router = useRouter()
  const userStore = useSysUserStore()
  const tree = shallowRef<RouteRecordRaw[]>([])
  const routeList = computed(()=>router.getRoutes().filter(a=>a.path.startsWith(basePath)))
  watchImmediate(routeList, (routes)=>{
    const newTree : RouteRecordRaw[] = []
    routes = routes.filter(a=>!a.redirect)
    //按路径深度分组 { 1: [...route], 2: [...] }
    const groups = groupBy(routes, a=>getRouteLevel(a)+'')
    //对每个分组的子项进行排序
    for (const key in groups) {
      groups[key] = sortBy(groups[key], 'meta.sort')
    }
    // console.log(groups);
    //将每个数组展开为一维数组
    routes = Object.values(groups).flatMap(a=>[...a])
    
    function findParent(path: string, list: RouteRecordRaw[]) {
      for (const item of list) {
        if (isChild(path, item.path)) {
          return item
        }
        if (item.children.length) {
          const val = findParent(path, item.children)
          if (val) {
            return val
          }
        }
      }
    }
    //遍历按深度从小到大排序后的路由列表
    for (const route of routes) {
      // console.log(route);
      let perm = route.meta.perm
      if (!isNil(perm)) {
        const curLevel = getRouteLevel(route)
        //从当前位置往前找->上级路由->上上级路由
        //然后将权限标识进行拼接
        routes.find(prev=>{
          if (route.path.startsWith(prev.path) 
            && getRouteLevel(prev) < curLevel 
            && !isNil(prev.meta.perm)) {
              perm = prev.meta.perm + ':' + perm
          }
        })
        if (!userStore.hasPermission(perm)) {
          continue
        }
      }
      const item = cloneDeep(route)
      //在已添加的结构中寻找父级
      const parent = findParent(route.path, newTree)
      if (parent) {
        parent.children.push(item)
      } else {
        newTree.push(item)
      }
    }
    tree.value = newTree
    // console.log(tree.value);
  }, { deep: true })
  return { tree }
}

/**
 * 获取路由的层级
 */
function getRouteLevel(route: RouteRecordRaw) {
  return route.path.split('/').length
}