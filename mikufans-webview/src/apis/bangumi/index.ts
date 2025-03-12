import { request } from "../service"

export default {
  getById, 
  search, 
  create, 
  update, 
  remove, 
  subscribed, 
  subscribe, 
  unsubscribe,
  indexes,
  animeFastLinks
}

function getById(id: string) {
  return request<Bangumi>({
    url: '/bangumi/' + id,
    method: 'get',
  })
}

function search(params: BangumiParams) {
  return request<Page<Bangumi>>({
    url: '/bangumi/search',
    method: 'get',
    params
  })
}

function create(data: BangumiDto) {
  return request<Bangumi>({
    url: '/bangumi',
    method: 'post',
    data
  })
}

function update(data: BangumiDto) {
  return request<Bangumi>({
    url: '/bangumi',
    method: 'put',
    data
  })
}

function remove(id: string) {
  return request<Bangumi>({
    url: '/bangumi/' + id,
    method: 'delete',
  })
}

function subscribed(id: string) {
  return request<boolean>({
    url: '/bangumi/subscribe/' + id,
    method: 'get',
  })
}

function subscribe(id: string) {
  return request<void>({
    url: '/bangumi/subscribe/' + id,
    method: 'post',
  })
}

function unsubscribe(id: string) {
  return request<boolean>({
    url: '/bangumi/subscribe/' + id,
    method: 'delete',
  })
}
function indexes(type: VideoType) {
  return request<{
    sorts: Option[],
    filters: { 
      label: string, 
      query: string, 
      options: Option[]
    }[]
  }>({
    url: '/bangumi/indexes',
    method: 'get',
    params: { type }
  })
}
function animeFastLinks() {
  return request<{ 
    label: string, 
    query: string, 
    options: Option[]
  }[]>({
    url: '/bangumi/anime/fastlinks',
    method: 'get',
  })
}