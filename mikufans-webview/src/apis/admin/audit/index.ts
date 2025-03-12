import { request } from "@/apis/service";

export default {
  submitAudit,
  searchVideo,
  getVideoAudit,
}

function submitAudit(data: SysAuditDto) {
  return request<SysAudit>({
    url: '/admin/system/audit',
    method: 'put',
    data
  })
}

function searchVideo(params?: SearchParams) {
  return request<Page<VideoAudit>>({
    url: '/admin/video/audit',
    method: 'get',
    params
  })
}

function getVideoAudit(videoId: string) {
  return request<VideoAudit>({
    url: '/admin/video/audit/' + videoId,
    method: 'get',
  })
}