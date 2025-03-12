import { request } from "../../service";

export default {
  search, create, update, remove, changeOrder
}

function search(params: UserAlbumSearch) {
  return request<Page<UserAlbum>>({
    url: '/user/album/search',
    method: 'get',
    params
  })
}

function create(data: UserAlbumDto) {
  return request<UserAlbum>({
    url: '/user/album',
    method: 'post',
    data
  })
}

function update(data: UserAlbumDto) {
  return request<void>({
    url: '/user/album',
    method: 'put',
    data
  })
}

function remove(...ids: string[]) {
  return request<void>({
    url: '/user/album/' + ids,
    method: 'delete',
  })
}

function changeOrder(data: OrderDto) {
  return request<void>({
    url: '/user/album/order',
    method: 'put',
    data
  })
}