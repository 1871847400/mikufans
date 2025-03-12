type USER_FLAGS = 
  | 'PUBLIC_UPLOAD'
  | 'PUBLIC_STAR'
  | 'PUBLIC_FOLLOW'
  | 'PUBLIC_FANS'
  | 'PUBLIC_SUBSCRIBE'

interface UserFlag {
  name: string //key
  display: string //中文名
  defValue: any
}