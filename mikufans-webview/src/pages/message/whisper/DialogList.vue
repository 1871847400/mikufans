<template>
  <div class="w-[200px] h-full bg0 flex flex-col">
    <div class="title">会话列表</div>
    <ul v-if="list.length===0&&done" class="m-auto grey2">最近没有消息</ul>
    <el-scrollbar v-else>
      <template v-for="i in list.toSorted((a,b)=>b.top-a.top)" :key="i.id">
        <li @contextmenu="onContextMenu($event, i)" @click="targetId=i.targetId" class="tab-item" :active="targetId==i.targetId" :top="i.top" :unread="i.unread>99 ? '99+' : i.unread">
          <user-avatar class="mx-2 pointer-events-none" :user="i.target" size="50px" :popover="false"/>
          <div>
            <div class="maxline-1 leading-8">{{ i.target.nickname }}</div>
            <div v-if="i.lastWhisper" class="maxline-1 text-xs grey2">{{ showMessage(i.lastWhisper) }}</div>
          </div>
          <i class="iconfont remove" title="删除会话" @click="remove(i.id)">&#xe647;</i>
        </li>
      </template>
      <div v-if="loading" v-loading="loading">加载中</div>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import userDialogApi from '@/apis/user/message/dialog';
import { usePage } from '@/hooks/usePage';
import ContextMenu from '@imengyu/vue3-context-menu';
const router = useRouter()
const { targetId } = toRefs(useMsgStore())
const { useSocketEvent } = useMsgStore()
const userStore = useUserStore()

watchImmediate(targetId, async targetId=>{
  if (targetId && targetId !== '0' && !userStore.isSelf(targetId)) {
    const dialog = await userDialogApi.createTemp(targetId)
    if (dialog && !list.value.some(a=>a.targetId===dialog.targetId)) {
      list.value.unshift(dialog)
    }
  }
})

function search(pageNum: number) {
  return userDialogApi.search({
    pageNum,
    pageSize: 10
  })
}
const { list, loading, done } = usePage(search, {
  shallowRef: false,
  immediate: [1],
  // delay: 3000,
  compare(a, b) {
    return a.id === b.id || a.targetId === b.targetId
  },
})
useSocketEvent('new_whisper', (whisper: UserWhisper)=>{
  const dialog = list.value.find(a=>a.targetId===whisper.receiverId||a.targetId===whisper.userId)
  if (dialog) {
    dialog.lastWhisper = whisper
  }
})
useSocketEvent('revoke_whisper', (id: string)=>{
  const data = list.value.find(a=>a.lastWhisper?.id===id)
  if (data) {
    data.lastWhisper.revoked = 1
  }
})
useSocketEvent('new_dialog', (dialog: UserDialog)=>{
  const old = list.value.find(a=>a.targetId===dialog.targetId)
  if (old) {
    Object.assign(old, dialog)
  } else {
    list.value.unshift(dialog)
  }
})
function onContextMenu(e: MouseEvent, i: UserDialog) {
  e.preventDefault();
  ContextMenu.showContextMenu({
    x: e.x,
    y: e.y,
    items: [
      { 
        label: i.top ? '取消置顶' : '置顶', 
        async onClick() {
          const val = (i.top+1)%2
          await userDialogApi.setTop(i.id, val)
          list.value.forEach(a=>a.top=0)
          i.top = val
        }
      },
      { 
        label: '删除', 
        onClick() {
          remove(i.id)
        },
      },
    ]
  })
}
function showMessage(whisper: UserWhisper) {
  if (whisper.revoked === 1) {
    return (userStore.isSelf(whisper.userId) ? '你' : '对方') + '撤回了一条消息'
  }
  return whisper.msgType===1 ? whisper.message : '[图片]'
}
async function remove(id: string) {
  await userDialogApi.removeById(id)
  list.value = list.value.filter(a=>a.id!==id)
  //如果当前打开的会话和删除的会话一样则切换到会话列表中的第一个
  if (list.value.length) {
    router.replace({ hash: '#' + list.value[0].targetId })
  } else {
    router.replace({ hash: null })
  }
}
</script>

<style scoped lang="scss">
.title {
  width: 100%;
  line-height: 36px;
  font-size: 13px;
  text-indent: 12px;
  border-bottom: 1px solid var(--border-color);
  border-right: 1px solid var(--border-color);
}
.tab-item {
  width: 100%;
  height: 75px;
  box-sizing: border-box;
  cursor: pointer;
  user-select: none;
  display: flex;
  gap: 8px;
  padding: 0 8px;
  align-items: center;
  font-size: 16px;
  position: relative;
  .remove {
    display: none;
    transition: all .2s;
    &:hover {
      color: var(--blue1);
      font-size: 18px;
    }
  }
  &:hover, &[active='true'] {
    background-color: var(--bg2);
    .remove {
      display: block;
    }
  }
  &[top='1']::before {
    content: '置顶';
    position: absolute;
    font-size: 10px;
    color: var(--pink0);
    left: 0px;
    top: 4px;
    font-weight: 550;
    border: 2px solid var(--pink0);
    border-radius: 4px;
    padding: 0 1px;
    transform: rotate(-35deg);
  }
  &[unread='0']::after {
    display: none;
  }
  &::after {
    content: attr(unread);
    position: absolute;
    width: 20px;
    line-height: 20px;
    text-align: center;
    right: 4px;
    top: 50%;
    font-size: 12px;
    color: #fff;
    font-weight: bold;
    transform: translateY(-50%);
    background-color: red;
    border-radius: 50%;
  }
}
.remove {
  position: absolute;
  right: 4px;
  top: 4px;
  font-size: 12px;
  color: #777;
  transition: all .2s;
}
</style>