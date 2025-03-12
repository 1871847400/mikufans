import { openDialog } from "@/utils/dialog"
import UserLoginDialog from '@/components/UserLoginDialog/index.vue';
import userLikeApi from '@/apis/user/like'

/**
 * 通用点赞hook
 */
export function useLike(likeData: MaybeRef<LikeStatus>){
  const userStore = useUserStore()
  const submitting = ref(false)
  function like() {
    switch (unref(likeData).likeVal) {
      case 0:
      case 2:
        save(1, '点赞成功')
        break
      case 1:
        save(0)
        break
    }
  }
  function dislike() {
    switch (unref(likeData).likeVal) {
      case 0:
      case 1:
        save(2, '点踩成功')
        break
      case 2:
        save(0)
        break
    }
  }
  async function save(likeVal: number, msg?: string) {
    if (!userStore.isLogin) {
      return openDialog(UserLoginDialog)
    }
    if (submitting.value) {
      return
    }
    submitting.value = true
    try {
      const data = await userLikeApi.like(unref(likeData).id, likeVal)
      Object.assign(unref(likeData), data)
      if (msg) {
        message.success(msg)
      }
    } finally {
      submitting.value = false
    }
  }
  return { like, dislike, submitting }
}