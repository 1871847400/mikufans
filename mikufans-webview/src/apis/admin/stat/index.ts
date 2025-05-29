import { request } from "@/apis/service";

export type StatType = 
'USER_REGISTER' |
'VIDEO_UPLOAD' |
'ARTICLE_UPLOAD' |
'PUBLISH_SEND' |
'DANMU_SEND' |
'COMMENT_SEND'

export type StatPeriod = 'DAY' | 'WEEK' | 'MONTH' | 'YEAR' | 'TOTAL'
export function getStatPeriod() {
  return request<Record<StatType, {
    title: string,
    icon: string,
    counts: Record<StatPeriod, number>
  }>>({
    method: 'get',
    url: '/admin/stats/period',
  })
}

export function getStatTrend(params: {
  type: StatType,
  start: string,
  end: string,
}) {
    return request<Record<string, number>>({
    method: 'get',
    url: '/admin/stats/trend',
    params
  })
}