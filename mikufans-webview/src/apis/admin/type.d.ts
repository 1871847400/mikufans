interface SysUser extends BaseEntity {
  username: string
  password?: string
  avatarId: string
  disabled: number
  roleIds: string[]
  roles: string[]
  perms: string[]
}

interface SysRole extends BaseEntity {
  roleKey: string
  roleName: string
  permIds: string[]
}

interface SysPerm extends BaseEntity {
  pid: string
  permKey: string
  permName: string
}

interface SysParam extends BaseEntity {
  paramKey: string
  paramValue: string
}

interface SysNotice extends BaseEntity {
  noticeType: string
  title: string
  template: string
  url: string
  enableTime: string
  expireTime?: string
}

interface ServerInfo {
  cpu: Record<string, any>
  mem: Record<string, any>
  sys: Record<string, any>
  disk: Record<string, any>
  jvm: Record<string, any>
  env: Record<string, string>
}

interface OnlineUser {
  id: string
  userType: number
  username: string
  ipaddr: string
  location: string
  time: string
}