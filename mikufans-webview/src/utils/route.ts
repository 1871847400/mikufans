import router from "@/router"
import { clone, cloneDeep, difference, intersection, isEqual, toInteger, toNumber } from "lodash"
import { diff } from "./common"

export function getRouteQuery<T>(key: string, defVal: T) : T {
  const route = router.currentRoute
  let value = route.value.query[key]
  if (value) {
    if (Array.isArray(value)) {
      value = value[0]
    }
    if (typeof defVal === 'number') {
      return toNumber(value) as T
    } else if (typeof defVal === 'string') {
      return (value+'') as T
    }
    return value as T
  }
  return defVal
}

export function getStrQuery(key: string, defVal?: string) {
  const route = router.currentRoute
  let value = route.value.query[key]
  if (value) {
    if (Array.isArray(value)) {
      value = value[0]
    }
    return value
  }
  return defVal
}

export function getIntQuery(key: string, defVal?: number) {
  const route = router.currentRoute
  let value = route.value.query[key]
  if (value) {
    if (Array.isArray(value)) {
      value = value[0]
    }
    return toInteger(value)
  }
  return defVal
}

export function getBoolQuery(key: string, defVal?: boolean) {
  const route = router.currentRoute
  let value = route.value.query[key]
  if (value) {
    if (Array.isArray(value)) {
      value = value[0]
    }
    return !!value
  }
  return defVal
}

export interface RouteQuriesOptions<T extends object> {
  /**
   * 是否在数据变化后立即改变路由
   * 如果为true代表任何属性变化后都会
   * 如果为[],代表其中的属性变化后才改变
   */
  autoCommit?: boolean | (keyof T)[]
}

/**
 * 当前参数发生变化时自动更新路由
 * 会清除其他未指定的参数
 * @param defData 不要使用二级以上对象
 */
export function useRouteQueries<T extends object>(defData: T, options?: RouteQuriesOptions<T>) {
  options ??= {
    autoCommit: true
  }
  const data = reactive(clone(defData))
  const route = useRoute()
  const router = useRouter()
  watchImmediate(()=>route.query, (query)=>{
    for (const key in query) {
      if (Object.hasOwn(data, key)) {
        data[key] = query[key]
      }
    }
  })
  let prevData = clone(defData)
  watch(data, ()=>{
    if (options.autoCommit === true) {
      commit()
    } else if (Array.isArray(options.autoCommit)) {
      const diffs = diff(data, prevData)
      if (intersection(options.autoCommit, diffs).length) {
        commit()
      }
    }
    prevData = clone(unref(toRaw(data)))
  })
  function reset(autoCommit = true) {
    Object.assign(data, defData)
    if (autoCommit) {
      commit()
    }
  }
  function commit() {
    const queries = clone(toRaw(data))
    for (const key in queries) {
      if (isEqual(data[key], defData[key]) 
        || queries[key] === null || queries[key]===undefined) {
        delete queries[key]
      }
    }
    router.push({
      query: {
        ...queries
      },
      force: true,
    })
  }
  return {
    queries: data,
    reset,
    commit,
  }
}