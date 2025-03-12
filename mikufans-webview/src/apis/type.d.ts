interface R<T> {
  readonly code: number
  readonly msg: string
  readonly data: T
}

interface Page<T> {
  /**
   * 满足条件的总记录数,需要后端searchCount=true否则为0
   */
  total: number
  /**
   * 每页大小
   */
  size: number
  /**
   * 当前页码
   */
  current: number
  /**
   * 当前页码的记录列表
   */
  records: Array<T>
  /**
   * 总页数,需要后端searchCount=true否则为0
   */
  pages: number
  /**
   * records 是否为空
   */
  empty: boolean
}

/**
 * 通用数据库实体属性
 */
interface BaseEntity {
  id: string
  userId: string
  createTime: string
  user: {
    id: string
    nickname: string
    avatarId: string
    level: number
    uri: string
  }
  /* 管理员才能拿到以下属性 */
  updateTime?: string
  remark?: string
  createBy?: string 
  updateBy?: string
  createByName?: string
  updateByName?: string
}

//通用分页搜索参数
interface SearchParams {
  pageNum?: number //默认1
  pageSize?: number //默认10
  searchCount?: boolean //是否查询数量,默认true
}

interface Option {
  label: string
  value: string|number|boolean
}

type BusinessType = 
  'VIDEO' |
  'VIDEO_PART'|
  'ANIME'|
  'MOVIE'|
  'DYNAMIC'|
  'COMMENT'|
  'DANMU'|
  'PUBLISH'|
  'ARTICLE'|
  'RATE'


interface QueryOption {
  query: string
  value: string
  options: Option[]
}