import { request } from "../service";

export default {
  create, 
  update, 
  remove, 
  getFlags, 
  search,
  getRecommend,
  getById,
  payCoin,
  createShare,
  useShare,
  getCompletion,
  getLikes,
  getUploadList,
  getUploadCount,
  getRelatedList,
  apply,
}
/**
 * 更新视频信息
 */
function update(data: Partial<VideoDto> & { id: string }) {
  return request<Video>({
    url: '/video',
    method: 'put',
    data,
  })
}
/**
 * 创建视频
 */
function create(data: VideoDto) {
  return request<Video>({
    url: '/video',
    method: 'post',
    data,
  })
}
/**
 * 删除视频
 */
function remove(videoId: string) {
  return request<void>({
    url: '/video/' + videoId,
    method: 'delete',
  })
}
/**
 * 获取视频标记
 */
function getFlags(videoId: string, partId?: string) {
  return request<Record<string,string>>({
    url: '/video/flag/' + videoId,
    method: 'get',
    params: { partId }
  })
}
/**
 * 搜索符合条件的视频
 */
function search(params: VideoSearchParams) {
  return request<Page<Video>>({
    url: '/video/search',
    method: 'get',
    params,
  })
}
/**
 * 最近点赞的视频
 */
function getLikes(params: SearchParams & { userId: string }) {
  return request<Page<Video>>({
    url: '/video/like/' + params.userId,
    method: 'get',
    params,
  })
}
/**
 * 根据id或sid获取视频
 */
function getById(idOrSid: string) {
  return request<Video>({
    url: '/video/' + idOrSid,
    method: 'get',
  })
}
/**
 * 向视频投币
 */
export function payCoin(videoId: string, coin: string|number) {
  return request<void>({
    url: `/video/coin/${videoId}/${coin}`,
    method: 'post',
  })
}
/**
 * 创建视频分享码
 */
function createShare(videoId: string) {
  return request<string>({
    url: `/video/share/${videoId}`,
    method: 'post',
  })
}
/**
 * 使用视频分享码
 */
function useShare(code: string) {
  return request<string>({
    url: `/video/share/${code}`,
    method: 'get',
  })
}
/**
 * 获取随机推荐视频
 */
function getRecommend(params: {
  size: number
}) {
  return request<Video[]>({
    url: `/video/recommend`,
    method: 'get',
    params
  })
}
/**
 * 搜索词补全(来源视频标题)
 */
function getCompletion(prefix: string) {
  return request<HightlightText[]>({
    url: '/video/completion',
    method: 'get',
    params: {
      prefix
    }
  })
}
/**
 * 获取自己的上传列表
 * 默认按创建时间排序
 */
function getUploadList(params: UploadVideoSearchParams) {
  return request<Page<Video>>({
    url: '/video/upload',
    method: 'get',
    params,
  })
}
/**
 * 获取上传数量
 */
function getUploadCount(type?: VideoType) {
  return request<Record<UploadVideoSearchParams['status'], number>>({
    url: '/video/upload/count',
    method: 'get',
    params: {
      type
    }
  })
}
/**
 * 获得相关推荐视频
 */
function getRelatedList(vid: string, size?: number) {
  return request<Video[]>({
    url: '/video/relate/' + vid,
    method: 'get',
    params: {
      size
    }
  })
}
/**
 * 申请重新审核
 */
function apply(videoId: string) {
  return request<void>({
    url: '/video/apply/' + videoId,
    method: 'put',
  })
}