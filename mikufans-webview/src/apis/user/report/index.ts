import { request } from "@/apis/service";

export default {
  getOne,
  create,
  getBehaviors,
}

function getOne(targetId: string) {
  return request<UserReport | null>({
    url: '/user/report/'+targetId,
    method: 'get',
  })
}

function create(data: UserReportDto) {
  return request<void>({
    url: '/user/report',
    method: 'post',
    data
  })
}

function getBehaviors() {
  return request<ReportBehavior[]>({
    url: '/user/report/behaviors',
    method: 'get',
  })
}