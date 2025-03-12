export const useSpaceStore = defineStore('space', ()=>{
  const router = useRouter()
  const userStore = useUserStore()
  //显示的目标用户id
  const userId = ref('')
  //显示的目标用户数据
  const user = ref<User>()
  //是否为自己
  const isSelf = computed(()=>userStore.isSelf(userId.value))
  //切换页面
  function changePage(type: string) {
    return router.push(`/space/${userId.value}/${type}`)
  }
  //是否允许其他人查看
  function isAllowed(flag: USER_FLAGS) {
    return isSelf.value || user.value.flags[flag]+'' == 'true'
  }
  return {
    isSelf,
    userId,
    user,
    changePage,
    isAllowed
  }
})