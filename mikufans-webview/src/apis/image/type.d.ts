interface ImageResource extends BaseEntity {
  md5: string //md5
  url: string //本地保存地址
  transfer?: string //转储地址
  mediaType: string //媒体类型
  fileSize: number //文件大小
  mainColor: string //主色调
}

interface ImageFetchResult {
  type: 'LINK'|'CLOUD'|'NOT_FOUND'|'BASE64',
  content: string|string[]
  mimeType: string
}