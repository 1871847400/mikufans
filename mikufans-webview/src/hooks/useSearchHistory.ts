import { clone } from "lodash"

export function useSearchHistory(options={
  name: 'search-history',
  maxSize: 100,
  initialData: [] as string[], //初始数据
}) {
  const { name, maxSize, initialData } = options
  const list = ref<string[]>([])
  watchDeep(list, ()=>{
    localStorage.setItem(name, JSON.stringify(list.value))
  })
  const store = localStorage.getItem(name)
  if (store) {
    list.value = JSON.parse(store)
  } else {
    list.value = clone(initialData)
  }
  function addHistory(...keywords: string[]) {
    for (const keyword of keywords) {
      const index = list.value.indexOf(keyword)
      if (index !== -1) {
        list.value = list.value.toSpliced(index, 1)
        list.value.unshift(keyword)
      } else {
        if (maxSize !== -1 && maxSize >= list.value.length) {
          list.value = list.value.toSpliced(-1) //删除末尾元素
        }
        list.value.unshift(keyword) //在头部添加
      }
    }
  }
  function removeHistory(keyword: string) {
    list.value = list.value.filter(i=>i !== keyword)
  }
  return { histories: list, addHistory, removeHistory }
}