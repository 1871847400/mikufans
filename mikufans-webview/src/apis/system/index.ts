import { request } from "../service";

/**
 * 获取正在展示的轮播广告
 */
export function listCarousels(params: {
  position: SysCarousel['position'],
  channelId?: string
}) {
  return request<SysCarousel[]>({
    url: '/carousel/' + params.position,
    method: 'get',
    params,
    __cache__: true,
  })
}
/**
 * 列出支持的所有地区语言
 */
export function listRegions() {
  return request<SysRegion[]>({
    url: '/region',
    method: 'get',
    __cache__: true,
  })
}