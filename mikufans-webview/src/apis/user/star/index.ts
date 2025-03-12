import { request } from "@/apis/service";

export default {
  search, 
  create, 
  updateById, 
  removeById, 
  changeOrder, 
  hasStarVideo
}

function search(params: UserStarParams) {
  return request<UserStar[]>({
    url: '/user/star/search',
    method: 'get',
    params
  })
}

function create(data: UserStarDto) {
  return request<UserStar>({
    url: '/user/star',
    method: 'post',
    data
  })
}

function updateById(data: UserStarDto) {
  return request<boolean>({
    url: '/user/star',
    method: 'put',
    data
  })
}

function removeById(id: string){
  return request<boolean>({
    url: '/user/star/' + id,
    method: 'delete',
  })
}

function changeOrder(ids: string[]) {
  return request<void>({
    url: '/user/star/order',
    method: 'put',
    data: {
      ids
    }
  })
}

function hasStarVideo(videoId: string) {
  return request<boolean>({
    url: '/user/star/check/' + videoId,
    method: 'get',
  })
}