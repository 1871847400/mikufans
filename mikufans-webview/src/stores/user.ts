import { defineStore } from "pinia";
import { isBlank } from "@/utils/common";
import userApi from '@/apis/user';
import { getAccessToken, setAccessToken, setRefreshToken } from '@/utils/token';
import { MaybeRef } from "vue";
import UserLoginDialog from '@/components/UserLoginDialog/index.vue';
import { openDialog } from "@/utils/dialog";

//初始化数据,写成方法方便拷贝
//默认属性不可删,否则export会丢失响应性
function initialState() : User {
  return {
    id: null,
    username: '',
    nickname: '',
    disabled: 0,
    sign: '',
    birthday: null,
    coin: 0,
    gender: 0,
    avatarId: '0',
    registerTime: '',
    background: '',
    flags: {} as any,
    userId: '',
    user: null,
    exp: 0,
    level: 0,
    nextExp: 0,
    createTime: '',
    articles: 0,
    coins: 0,
    dynamics: 0,
    fans: 0,
    likes: 0,
    dislikes: 0,
    follows: 0,
    publishes: 0,
    subscribes: 0,
    videos: 0,
    uri: ''
  }
}
export const useUserStore = defineStore('user', ()=>{
  const isLogin = ref(false)
  const user = reactive<User>(initialState())
  function isSelf(userId: MaybeRef<any>) {
    return isLogin.value && unref(userId) === user.id && !isBlank(user.id)
  }
  async function getUserInfo(forceUpdate = false) : Promise<User|undefined> {
    if ((!isLogin.value || forceUpdate) && getAccessToken()) {
      const data = await userApi.fetch()
      if (data) {
        Object.assign(user, data)
        isLogin.value = true
        return user
      }
    } else if (isLogin.value) {
      return user
    }
  }
  function login(mode = 0) {
    openDialog(UserLoginDialog, { mode })
  }
  function logout() {
    Object.assign(user, initialState())
    setAccessToken(null)
    setRefreshToken(null)
    location.reload()
    logger.debug('退出登录', toRaw(user))
  }
  //用户主页背景列表
  const { state: backgrounds } = useAsyncState(userApi.getBackground(), {} as Record<string, string>)
  return {
    ...toRefs(user),
    isLogin,
    getUserInfo,
    backgrounds,
    isSelf,
    login,
    logout,
  }
})