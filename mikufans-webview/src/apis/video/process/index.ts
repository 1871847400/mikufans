import { request } from "../../service";

export function getProcessStatus() {
  return request<{ label: string, type: MessageType }>({
    url: '/video/process/status',
    method: 'get',
  })
}