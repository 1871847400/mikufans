import { request } from "@/apis/service";

export default {
  getById, 
  create, 
  search, 
  update, 
  removeById, 
  changeOrder, 
  getFirstCanplay,
}

function getById(id: string) {
  return request<VideoPart>({
    url: '/video/part/' + id,
    method: 'get',
  })
}

function create(data: VideoPartDto) {
  return request<VideoPart>({
    url: '/video/part',
    method: 'post',
    data,
  })
}

function update(data: Partial<VideoPartDto> & { id: string, sort?: number }) {
  return request<void>({
    url: '/video/part',
    method: 'put',
    data
  })
}

function search(params: {
  videoId: string
}) {
  return request<VideoPart[]>({
    url: '/video/part/search',
    method: 'get',
    params
  })
}

function getFirstCanplay(videoId: string) {
  return request<VideoPart>({
    url: '/video/part/first/' + videoId,
    method: 'get',
  })
}

function removeById(partId: string) {
  return request<boolean>({
    url: '/video/part/' + partId,
    method: 'delete',
  })
}

function changeOrder(id: string, sort: number) {
  return request<string[]>({
    url: '/video/part/order/'+id,
    method: 'put',
    params: {
      sort
    }
  })
}

// function getUpload(params: SearchParams & { videoId: string, status?: DoingStatus }) {
//   return request<Record<DoingStatus, Page<VideoPart>>>({
//     url: '/video/part/upload',
//     method: 'get',
//     params
//   })
// }