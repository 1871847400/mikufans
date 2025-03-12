import { request } from "@/apis/service";

export function importDanmus(data: { partId: string, userId: string, url: string }) {
  return request<string>({
    url: '/admin/video/danmu/import',
    method: 'post',
    data
  })
}