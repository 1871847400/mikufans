import { request } from "@/apis/service"

export default {
  search,
  list,
  audit
}

function search(params?: Partial<UserReport> & SearchParams) {
  return request<Page<UserReportVo>>({
    url: '/admin/user/report',
    method: 'get',
    params
  })
}

function list(targetId: string, params?: SearchParams) {
  return request<Page<UserReport>>({
    url: '/admin/user/report/' + targetId,
    method: 'get',
    params
  })
}

function audit(data: UserReportAudit) {
  return request<void>({
    url: '/admin/user/report/audit',
    method: 'put',
    data
  })
}