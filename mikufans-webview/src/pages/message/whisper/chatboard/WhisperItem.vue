<template>
  <div class="whisper-item">
    <div class="text-center text-xs">{{ _showTime }}</div>
    <div class="whisper-item-content" :style="{flexDirection:userStore.isSelf(userId)?'row-reverse':'row'}">
      <div v-if="revoked" class="grey2 mx-auto text-sm">{{ userStore.isSelf(userId) ? '你撤回了一条消息' : '对方撤回了一条消息' }}</div>
      <template v-else>
        <user-avatar class="mr-2 h-fit" :user="whisper.user" size="40px" :popover="false" />
        <div ref="contentRef" class="content" @contextmenu="onContextMenu">
          <div v-if="msgType===1" v-html="convertToHtml(msg)"></div>
          <miku-image v-else :res-id="msg" class="max-w-[200px] h-fit" preview/>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { convertToHtml } from '@/utils/emoji';
import { copyText } from '@/utils/copy';
import whisperApi from '@/apis/user/message/whisper'
import dayjs from 'dayjs';
import ContextMenu from '@imengyu/vue3-context-menu';
const props = defineProps<{
  whisper: UserWhisper,
  previous: UserWhisper,
}>()
const userStore = useUserStore()
const { revoked, message: msg, userId, msgType } = toRefs(props.whisper)
const contentRef = ref<HTMLElement>()
function onContextMenu(e: MouseEvent) {
  e.preventDefault();
  ContextMenu.showContextMenu({
    x: e.x,
    y: e.y,
    items: [
      {
        label: '复制',
        hidden: msgType.value!==1,
        onClick() {
          //鼠标选中的文字
          const selectText = document.getSelection().toString()
          if (selectText.length > 0) {
            copyText(selectText)
          } else {
            copyText(msg.value)
          }
          message.success('已复制到剪切板')
        }
      },
      {
        label: '撤回',
        hidden: !userStore.isSelf(userId.value),
        async onClick() {
          await whisperApi.revoke(props.whisper.id)
          message.success('撤回成功')
        },
      },
      {
        label: '保存',
        hidden: msgType.value!==2,
        onClick() {
          const img = contentRef.value.querySelector('img')
          if (img) {
            console.log(img);
            const link = document.createElement('a');
            link.download = 'mikufans-download.jpg';
            link.href = img.src
            link.click()
            link.remove()
            message.success('保存成功')
          } else {
            message.error('保存失败')
          }
        }
      }
    ]
  })
}

// previous防止和上一条消息显示一样的时间
const _showTime = computed(()=>{
  let a = showTime(props.whisper.sendTime)
  if (props.previous) {
    let b = showTime(props.previous.sendTime)
    if (a === b) {
      return ''
    }
  }
  return a
})
// 默认: 20:56
// 昨天: 昨天 23:50
// 前天: 11-13 23:50
// 去年: 2022-11-15 23:50
function showTime(time: string) {
  let now = dayjs()
  let date = dayjs(time)
  // if (diff.asSeconds() < 60) {
    //   return ''
    // }
  if (now.year() !== date.year()) {
    return date.format('YYYY-MM-DD HH:mm')
  }
  let diff = dayjs.duration(now.diff(date))
  if (diff.asHours() < 48) {
    if (date.day() !== now.day()) {
      return '昨天 ' + date.format('HH:mm')
    }
    return date.format('HH:mm')
  }
  return date.format('MM-DD HH:mm')
}
</script>

<style scoped lang="scss">
.whisper-item-content {
  display: flex;
  margin-bottom: 32px;
  .content {
    position: relative;
    margin-right: 8px;
    max-width: 400px;
    //保留连续的空白符,支持换行
    white-space: pre-wrap;
    background-color: #fff;
    border-radius: 4px;
    padding: 8px;
    margin-top: 4px;
    color: #333;
  }
}
.menu {
  position: absolute;
  background-color: #fff;
  border-radius: 4px;
  overflow: hidden;
  width: 100px;
  text-align: center;
  line-height: 24px;
  left: var(--left);
  top: var(--top);
  user-select: none;
  .option {
    cursor: pointer;
    &:hover {
      background-color: #ccc;
    }
  }
}
</style>