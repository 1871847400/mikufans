import { request } from "@/apis/service"

export default {
  listByUserId, saveDto, removeById, changeOrder
}

function listByUserId(userId: string) {
  return request<VideoRepresent[]>({
    url: '/video/represent/' + userId,
    method: 'get',
  })
}

function saveDto(data: VideoRepresentDto) {
  return request<VideoRepresent>({
    url: '/video/represent',
    method: 'post',
    data
  })
}

function removeById(id: string) {
  return request<boolean>({
    url: '/video/represent/' + id,
    method: 'delete',
  })
}

function changeOrder(data: OrderDto) {
  return request<void>({
    url: '/video/represent/order',
    method: 'put',
    data
  })
}