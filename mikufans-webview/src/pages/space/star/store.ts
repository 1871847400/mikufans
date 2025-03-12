import { openDialog } from '@/utils/dialog';
import UserStarDialog from '@/pages/space/star/menu/UserStarDialog.vue';
import userStarApi from '@/apis/user/star';

export const [ useProvide, useStore ] = createInjectionState(()=>{
  //当前空间用户所有收藏夹
  const userStars = ref<UserStar[]>([])
  const hash = useRouteHash('', { mode: 'push' })
  //当前选择的收藏夹
  const selectStar = computed({
    get() {
      const id = hash.value.replace('#', '')
      //第一项一般为默认收藏夹
      return userStars.value.find(a=>a.id===id) || userStars.value[0]
    },
    set(value) {
      if (value.defFlag == 1) {
        hash.value = ''
      } else {
        hash.value = '#' + value.id
      }
    }
  })
  function saveUserStar(userStar?: UserStar) {
    openDialog(UserStarDialog, {
      userStar,
      onSubmit(userStar: UserStar) {
        const oldData = userStars.value.find(s=>s.id===userStar.id)
        if (oldData) {
          Object.assign(oldData, userStar)
        } else {
          userStars.value = [userStars.value[0], userStar, ...userStars.value.toSpliced(0, 1)]
        }
      }
    })
  }
  async function deleteUserStar(userStar: UserStar) {
    await userStarApi.removeById(userStar.id)
    userStars.value = userStars.value.filter(a=>a.id!==userStar.id)
    //如果删除了正在显示的收藏夹
    if (selectStar.value?.id === userStar.id) {
      hash.value = ''
    }
    message.success('已删除收藏夹: ' + userStar.starName)
  }

  return {
    userStars,
    selectStar,
    saveUserStar,
    deleteUserStar
  }
})