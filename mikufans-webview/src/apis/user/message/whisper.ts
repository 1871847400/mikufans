import { request } from "../../service";

export default {
  create,
  search,
  revoke,
  stats,
}

function create(data: UserWhisperDto) {
  return request<UserWhisper>({
    url: '/user/whisper',
    method: 'post',
    data,
  })
}

function search(params: UserWhisperParams) {
  return request<Page<UserWhisper>>({
    url: '/user/whisper',
    method: 'get',
    params
  })
}

function revoke(id: string) {
  return request<void>({
    url: '/user/whisper/revoke/'+id,
    method: 'put',
  })
}

function stats(){
  return request<any>({
    url: '/whisper/stats',
    method: 'get',
  })
}