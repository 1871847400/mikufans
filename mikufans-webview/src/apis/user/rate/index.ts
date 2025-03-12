import { request } from "@/apis/service"

export default {
  getById,
  rate,
  listRate,
}

function getById(id: string) {
  return request<UserRate>({
    url: '/user/rate/' + id,
    method: 'get',
  })
}


function rate(data: UserRateDto) {
  return request<UserRate>({
    url: '/user/rate',
    method: 'post',
    data
  })
}

function listRate(id: string, params?: SearchParams) {
  return request<Page<UserRate>>({
    url: '/user/rate/search/' + id,
    method: 'get',
    params
  })
}