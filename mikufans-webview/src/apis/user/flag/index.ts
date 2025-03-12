import { request } from "../../service";

export default {
  listUserFlags,
  getUserFlags,
  saveUserFlag,
}

function listUserFlags() {
  return request<Record<USER_FLAGS, UserFlag>>({
    url: '/user/flag/list',
    method: 'get',
  })
}

function getUserFlags(userId: string) {
  return request<Record<USER_FLAGS, string>>({
    url: '/user/flag/list/' + userId,
    method: 'get',
  })
}

function saveUserFlag(data: { flagKey: USER_FLAGS, flagValue: string }) {
  return request<Record<USER_FLAGS, string>>({
    url: '/user/flag',
    method: 'put',
    data
  })
}