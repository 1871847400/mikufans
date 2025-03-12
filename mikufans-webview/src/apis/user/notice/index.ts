import { request } from "@/apis/service";

export default {
  search
}

function search(params: SearchParams) {
  return request<Page<UserNotice>>({
    url: '/user/notice',
    method: 'get',
    params
  })
}