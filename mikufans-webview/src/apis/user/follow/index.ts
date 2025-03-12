import { request } from "@/apis/service";

export default {
  follow,
  unfollow,
  getStatus,
  getFollows,
  getFans
}

function getStatus(targetId: string) {
  return request<UserFollowStatus>({
    url: '/user/follow/peek/' + targetId,
    method: 'get',
  })
}

function follow(targetId: string) {
  return request<UserFollowStatus>({
    url: '/user/follow/' + targetId,
    method: 'post',
  })
}

function unfollow(targetId: string) {
  return request<void>({
    url: '/user/follow/' + targetId,
    method: 'delete',
  })
}

function getFollows(params: UserFollowSearch) {
  return request<Page<UserFollowVo>>({
    url: '/user/follow/list',
    method: 'get',
    params
  })
}

function getFans(params: SearchParams & { userId?: string }) {
  return request<Page<UserFollowVo>>({
    url: '/user/follow/fans',
    method: 'get',
    params,
  })
}