import { useSearchHistory } from "@/hooks/useSearchHistory"
import searchHistoryApi from '@/apis/search';
import videoApi from '@/apis/video';

export const useSearchStore = defineStore('search', ()=>{
  const route = useRoute()
  const router = useRouter()
  const userStore = useUserStore()
  const inputRef = ref<HTMLInputElement>()
  //搜索输入框的内容
  const keyword = ref(route.query.kw?.toString()??'')
  //本地搜索记录(未登录时使用)
  const { histories, addHistory, removeHistory } = useSearchHistory({ 
    name: 'search-history',
    maxSize: 20,
    initialData: []
  })
  //当用户登录后使用服务器搜索记录
  const serverHistories = ref<string[]>([])
  watchImmediate(()=>userStore.isLogin, async (isLogin)=>{
    if (isLogin) {
      const page = await searchHistoryApi.search({pageNum: 1, pageSize: 20})
      serverHistories.value = page.records.map(a=>a.content)
    } else {
      serverHistories.value = []
    }
  })
  //显示的搜索记录
  const list = computed<string[]>(()=>userStore.isLogin ? serverHistories.value : histories.value)
  //删除搜索记录
  function remove(kw: string) {
    if (userStore.isLogin) {
      serverHistories.value = serverHistories.value.filter(a=>a!==kw)
      searchHistoryApi.remove(kw)
    } else {
      removeHistory(kw)
    }
  }
  //清空搜索记录
  function clear() {
    if (userStore.isLogin) {
      serverHistories.value = []
      searchHistoryApi.removeAll()
    } else {
      histories.value = []
    }
  }
  //当搜索内容变化时,更新补全列表
  const tipIndex = ref(-1)
  const tipList = ref<HightlightText[]>([])
  const __keyword = useRouteQuery('kw')
  watchDebounced(keyword, async ()=>{
    tipIndex.value = -1
    tipList.value = await videoApi.getCompletion(keyword.value)
  }, { debounce: 500, maxWait: 3000 })
  async function search(kw?: string) {
    kw ??= tipList.value[tipIndex.value]?.rawText
    kw ??= keyword.value
    if (route.name==='search') {
      //如果已经在搜索页面了只改变关键字
      __keyword.value = kw
    } else {
      await router.push({
        name: 'search',
        params: {
          type: 'all'
        },
        query: {
          kw
        },
        force: true, //防止push同一链接报错
      })
    }
    keyword.value = kw
    if (document.hasFocus() && document.activeElement instanceof HTMLElement) {
      document.activeElement.blur()
    }
    if (kw) {
      if (userStore.isLogin) {
        serverHistories.value = serverHistories.value.filter(a=>a!==kw)
        serverHistories.value.unshift(kw)
        searchHistoryApi.create(kw)
      } else {
        //如果用户没有登录,记录到本地
        addHistory(kw)
      }
    }
  }

  return {
    keyword,
    list,
    search,
    remove,
    clear,
    tipList,
    tipIndex,
    inputRef,
  }
})