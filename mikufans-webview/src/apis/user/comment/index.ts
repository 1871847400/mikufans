import { request } from "@/apis/service";

export default {
  removeById,
  create,
  getById,
  getByIds,
  getTreeById,
  search,
  stats,
  readAll,
  setTop,
  setSelected
}

/**
 * 分页搜索符合条件的评论
 */
function search(params: UserCommentParams) {
  return request<Page<UserComment>>({
    url: '/user/comment',
    method: 'get',
    params,
  })
}
/**
 * 给视频发送评论
 */
function create(data: UserCommentDto) {
  return request<UserComment>({
    url: '/user/comment',
    method: 'post',
    data
  })
}
/**
 * 获取指定评论的信息
 */
function getById(id: string) {
  return request<UserComment>({
    url: '/user/comment/' + id,
    method: 'get',
  })
}
/**
 * 获取一组评论,没查到的下标为null,例如 [1,2,3] => [obj,null,obj]
 */
function getByIds(ids: string[]) {
  return request<UserComment[]>({
    url: '/user/comment/list',
    method: 'get',
    params: {
      ids
    }
  })
}
/**
 * 获取评论树,拿到根节点
 */
function getTreeById(id: string) {
  return request<UserComment>({
    url: `/user/comment/tree/${id}`,
    method: 'get',
  })
}
/**
 * 删除指定评论
 */
function removeById(id: string) {
  return request<boolean>({
    url: '/user/comment/' + id,
    method: 'delete',
  })
}
/**
 * 统计信息
 */
function stats(){
  return request<UserCommnetStats>({
    url: '/user/comment/stats',
    method: 'get',
  })
}
/**
 * 设置已读状态
 * @param ids id集合
 * @param type 0 评论 1 at
 * @returns 至少有一个成功
 */
function readAll(ids: string[], type: number = 0){
  return request<boolean>({
    url: '/user/comment/read/' + ids,
    method: 'put',
    params: {
      type
    }
  })
}
/**
 * 评论置顶
 */
function setTop(commentId: string, top: boolean) {
  return request<void>({
    url: '/user/comment/top/' + commentId,
    method: 'put',
    params: { 
      top 
    }
  })
}
/**
 * 评论精选
 */
function setSelected(commentId: string, selected: boolean) {
  return request<void>({
    url: '/user/comment/select/' + commentId,
    method: 'put',
    params: {
      selected
    }
  })
}