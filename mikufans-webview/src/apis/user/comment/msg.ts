import { request } from "@/apis/service";

export default {
  search
}

/**
 * 获取评论消息
 */
function search(params: CommentMsgParams) {
  return request<Page<UserCommentMsg>>({
    url: '/user/comment/msg',
    method: 'get',
    params,
  })
}
/**
 * 隐藏通知
 */
function hidden() {
  return request<void>({
    url: '/user/comment/msg/hidden',
    method: 'put',
  })
}