import { request } from "@/apis/service"

export default {
  getSubtitleText,
  removeById
}

function getSubtitleText(id: string) {
  return request<string>({
    url: `/video/subtitle/content/${id}`,
    method: 'get',
  })
}

/**
 * 删除字幕文件(物理删除)
 */
function removeById(id: string) {
  return request({
    url: `/video/subtitle/${id}`,
    method: 'delete',
  })
}