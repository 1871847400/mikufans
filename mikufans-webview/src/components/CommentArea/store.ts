import { usePage } from "@/hooks/usePage"
import commentApi from '@/apis/user/comment';

export interface StoreParams {
  area: UserCommentArea //评论区
  useHash?: boolean //是否使用hash定位功能
}

export const [ useProvideStore, useStore ] = createInjectionState((params: StoreParams)=>{
  //选中的回复的评论对象
  const answering = shallowRef<UserComment>(null)
  //刚发送的评论
  const sendList = ref<UserComment[]>([])
  //刚删除的评论
  const removeList = ref<UserComment[]>([])
  //评论排序方式
  const order = ref<0|1>(0)
  //搜索评论的请求
  const request = usePage(search, {
    shallowRef: false,
    compare(a, b) {
      return a.id === b.id  
    },
  })
  const pageSize = 10
  function search(pageNum: number) {
    return commentApi.search({
      // targetId: params.target.id,
      // targetType: params.targetType,
      areaId: params.area.id,
      order: order.value,
      replyId: '0',
      pageNum,
      pageSize,
      hots: 2,
    })
  }
  //清空评论并加载第一页
  function reset() {
    request.list.value = []
    sendList.value = []
    answering.value = null
    request.run(1)
  }
  //切换排序时重新加载评论
  watch(order, reset)
  return {
    ...params,
    pageSize,
    answering,
    sendList,
    removeList,
    request,
    order,
    reset,
  }
})