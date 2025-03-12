import { ElMessage, ElMessageBox, ElMessageBoxOptions, MessageHandler, MessageOptions } from "element-plus";
import { openDialog } from "../dialog";
import ConfirmDialog from '@/components/ConfirmDialog/index.vue'
import { uniqueId } from "lodash";

const duration = 3000
const messages = new Array<{
  id: string
  message: string
  handler: MessageHandler
}>()
function show(type: MessageOptions['type'], message: string) {
  logger.debug(message)
  messages.filter(a=>a.message===message)
    .forEach(a=>a.handler?.close())
  const id = uniqueId()
  const handler = ElMessage({
    type,
    message,
    duration,
    plain: true,
    onClose() {
      messages.splice(messages.findIndex(a=>a.id===id), 1)
    },
  })
  messages.push({ id, message, handler })
}

export default {
  info(message: string) {
    show('info', message)
  },
  success(message: string) {
    show('success', message)
  },
  warning(message: string) {
    show('warning', message)
  },
  error(message: string) {
    show('error', message)
  },
  confirm(message: string, onCancel?: ()=>void) {
    return new Promise<void>((resolve) => {
      openDialog(ConfirmDialog, {
        message,
        onSubmit(confirm) {
          if (confirm) {
            resolve()
          } else {
            onCancel?.call(null)
          }
        }
      })
    })
  },
  prompt(message: string, options?: ElMessageBoxOptions) {
    return new Promise<string>((resolve, reject) => {
      ElMessageBox.prompt(message,'', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        closeOnClickModal: false,
        ...options
      }).then(data=>{
        if (data.action == 'confirm') {
          resolve(data.value)
        }
      })
      .catch(()=>{})//catch抑制不catch导致的报错
    })
  },
}