import { request } from "@/apis/service";

export default {
  like,
  getLikeData,
  getMsg,
  getLikeUsers,
}

function like(likeDataId: string, likeVal: number) {
  return request<LikeStatus>({
    url: '/user/like/' + likeDataId,
    method: 'put',
    params: {
      likeVal
    }
  })
}

function getLikeData(likeDataId: string) {
  return request<UserLikeData>({
    url: '/user/like/' + likeDataId,
    method: 'get',
  })
}

function getMsg(params: SearchParams) {
  return request<Page<UserLikeData>>({
    url: '/user/like/msg',
    method: 'get',
    params
  })
}

function getLikeUsers(likeDataId: string, params: SearchParams) {
  return request<Page<UserLike>>({
    url: '/user/like/list/' + likeDataId,
    method: 'get',
    params
  })
}