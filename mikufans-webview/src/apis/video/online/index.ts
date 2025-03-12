import { request } from "@/apis/service"

export default {
  getOnline
}

function getOnline(videoId: string) {
  return request<number>({
    url: '/video/online/' + videoId,
    method: 'get',
  })
}