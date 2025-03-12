import { request } from "@/apis/service";

export default {
  create,
  getById,
  removeById,
}

function create(data: UserPublishDto) {
  return request<UserPublish>({
    url: '/user/publish',
    method: 'post',
    data
  })
}

function getById(id: string) {
  return request<UserPublish>({
    url: '/user/publish/' + id,
    method: 'get',
  })
}

function removeById(id: string) {
  return request<boolean>({
    url: '/user/publish/' + id,
    method: 'delete',
  })
}