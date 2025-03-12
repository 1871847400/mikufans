import { request } from "@/apis/service";

export default {
  getMsgUnread
}

function getMsgUnread() {
  return request<MsgUnread>({
    url: '/msg/unread',
    method: 'get',
  })
}