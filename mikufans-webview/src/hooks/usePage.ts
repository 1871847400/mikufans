import { sleep } from "@/utils/datetime"
import { cloneDeep, isEqual, isInteger, isNil, isNumber, uniqueId } from "lodash"
import { v4 as uuidv4 } from 'uuid';

export type Request<T, P extends any[]> = (pageNum: number, ...args: P)=>Promise<Page<T>>
type Options<T, P extends any[]> = {
  compare: (a:T, b:T)=>boolean //比较两个数据是否相等,防重复
  delay: number //模拟网络请求延迟
  onSuccess: (page: Page<T>, ...args: P)=>void //成功获取一页数据后
  onError: (err: any)=>void
  onAppend: (t: T)=>void //向list添加数据事件
  shallowRef: boolean //只响应xx.value=xx 并且会影响list组过渡动画
  immediate: [number, ...P] //立即执行参数
}
const defaultOptions : Options<any, any[]> = {
  compare: (a, b)=>isEqual(a, b), 
  delay: 0, 
  onSuccess: ()=>{},
  onError: ()=>{},
  onAppend: ()=>{},
  shallowRef: true,
  immediate: null
}

interface ExecuteOptions<P> {
  pageNum: number
  args: P
}
export function usePage<T, R extends Page<T>, P extends any[]>(request: Request<T, P>, options?: Partial<Options<T, P>>) {
  options = Object.assign(cloneDeep(defaultOptions), options)
  const loading = ref(false)
  //page初始值为null,方便判断是否为初次请求
  const page = options.shallowRef ? shallowRef<R>(null) : ref<R>(null)
  //存储每页的数据
  const list = options.shallowRef ? shallowRef<T[]>([]) : ref<T[]>([])
  //是否已经到最后一页
  const done = computed(()=>{
    const result = page.value as Page<any>
    return result && ( isNumber(result.pages) && result.pages <= result.current
    || result.records.length < result.size)
  })
  const error = ref(null)
  //最后一次的请求id,防止旧的响应结果覆盖新的响应结果
  //注意：最好的做法是用aixos.CancelToken取消上一次请求
  let lastId = '-1'
  async function exec(execOptions: ExecuteOptions<P>) {
    const { pageNum, args } = execOptions
    const id = lastId = uuidv4()
    try {
      loading.value = true
      error.value = null
      //模拟延迟
      if (options.delay > 0) {
        await sleep(options.delay)
      }
      //发送请求
      const result = await request(pageNum, ...args)
      //如果请求被覆盖,则放弃请求结果
      if (lastId !== id) {
        return
      }
      page.value = result
      const newList = [...list.value]
      for (const record of result.records) {
        if (newList.findIndex(i=>options.compare(i as T, record)) === -1) {
          newList.push(record)
          options.onAppend(record)
        }
      }
      list.value = newList as any
      options.onSuccess(result, ...args)
      return page
    } catch(err) {
      if (lastId !== id) {
        return
      }
      error.value = err
      options.onError(err)
      throw err
    } finally {
      //最后设置loading为false
      loading.value = false
    }
  }
  function run(pageNum: number, ...args: P) {
    return exec({ pageNum, args })
  }
  function prev(...args: P) {
    const pageNum = (page?.value?.current ?? 0) - 1
    if (pageNum >= 1) {
      return run(pageNum, ...args)
    }
  }
  function next(...args: P) {
    if (!done.value) {
      const pageNum = (page?.value?.current ?? 0) + 1
      return run(pageNum, ...args)
    }
  }
  function reset() {
    page.value = null
    error.value = null
    loading.value = false
    list.value = []
  }
  //立即执行
  if (options.immediate) {
    run(...options.immediate)
  }
  return {
    page, 
    list, 
    run, 
    prev,
    next,
    loading: readonly(loading),
    done,
    error,
    reset,
  }
}