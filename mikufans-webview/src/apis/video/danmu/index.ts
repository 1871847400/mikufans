import { baseURL, request } from "@/apis/service";

export default {
  listByPartId,
  send,
  remove,
  getBiliDanmuUrl,
  getColorList,
}

function listByPartId(partId: string, params?: { minId?: string }) {
  return request<VideoDanmu[]>({
    url: '/video/danmu/list/' + partId,
    method: 'get',
    params,
  })
}

function send(data: VideoDanmuDto) {
  return request<VideoDanmu>({
    url: '/video/danmu',
    method: 'post',
    data
  })
}

function remove(danmuId: string) {
  return request<void>({
    url: '/video/danmu/' + danmuId,
    method: 'delete',
  })
}

function getBiliDanmuUrl(cid: string) {
  return baseURL + '/video/danmu/bili/'+cid
}

function getColorList() {
  return request<VideoDanmuColor[]>({
    url: '/video/danmu/color',
    method: 'get',
  })
}