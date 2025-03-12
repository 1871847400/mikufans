import { request } from "@/apis/service";

export default {
  create,
  createTemp,
  removeById,
  removeByTargetId,
  search,
  setTop,
}

function create(targetId: string) {
  return request<UserDialog>({
    url: '/user/dialog/' + targetId,
    method: 'post',
  })
}

function createTemp(targetId: string) {
  return request<UserDialog>({
    url: '/user/dialog/temp/' + targetId,
    method: 'post',
  })
}

function removeById(id: string) {
  return request({
    url: '/user/dialog/' + id,
    method: 'delete',
  })
}

function removeByTargetId(targetId: string) {
  return request({
    url: '/user/dialog/target/' + targetId,
    method: 'delete',
  })
}

function search(params: SearchParams) {
  return request<Page<UserDialog>>({
    url: '/user/dialog/search',
    method: 'get',
    params
  })
}

function setTop(id: string, top: number) {
  return request({
    url: `/user/dialog/top/${id}/${top}`,
    method: 'put',
  })
}
