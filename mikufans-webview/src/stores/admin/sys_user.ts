import { get } from "@/apis/admin/common"
import { resetReactive } from "@/hooks/resetReactive"
import { getAdminToken, setAdminToken } from "@/utils/token"

export const useSysUserStore = defineStore('sys_user', ()=>{
  const isLogin = ref(false)
  const [ user, reset ] = resetReactive<SysUser>({
    id: null,
    username: '',
    avatarId: '0',
    roleIds: [],
    roles: [],
    perms: [],
    disabled: 0,
    createTime: '',
    user: null,
    userId: null,
  })
  function hasPermission(perm: any) {
    return perm && !!user.perms.includes(perm)
  }
  async function getUser() {
    if (!isLogin.value && getAdminToken()) {
      const data = await get<SysUser>('/admin/sys_user/')
      if (data) {
        Object.assign(user, data)
        isLogin.value = true
      }
    }
    return user
  }
  function logout() {
    isLogin.value = false
    setAdminToken(null)
    reset()
  }
  return {
    ...toRefs(user),
    isLogin,
    logout,
    getUser,
    hasPermission,
  }
})