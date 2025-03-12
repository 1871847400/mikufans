import { request } from "@/apis/service";

export default {
  save,
  search,
}

function save(data: VideoStarDto) {
  return request<boolean>({
    url: '/video/star',
    method: 'post',
    data,
  })
}

function search(params: VideoStarParams){
  return request<Page<VideoStar>>({
    url: '/video/star/search',
    method: 'get',
    params
  })
}