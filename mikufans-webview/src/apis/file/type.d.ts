type UploadStatus = 
  'UPLOADING' |
  'PAUSED' |
  'FAILURE' |
  'SUCCESS' |
  'CANCEL'

interface UploadTask extends BaseEntity {
  md5: string
  mediaType: string
  fileName: string
  fileSize: number
  uploadStatus: UploadStatus
  chunkCode: number
  chunkSize: number
  chunkCount: number
  uploadSize: number
  expireAt: string
  expired: boolean
}

interface UploadTaskDto {
  md5: string
  mediaType: string
  fileName: string
  fileSize: number
  chunkSize: number
}