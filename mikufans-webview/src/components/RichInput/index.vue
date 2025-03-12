<template>
  <div 
    ref="inputRef" 
    class="rich-input"
    :class="{ disabled }"
    :autofocus="autofocus" 
    :contenteditable="!disabled"
    :placeholder="placeholder"
    :is-empty="text==''"
    @beforeinput="onBeforeInput" 
    @input="onInput" 
    @keydown="onKeyDown"
    @paste="onPaste"
    @blur="onBlur"
  ></div>
</template>

<script setup lang="ts" name="RichInput">
import { convertEmoji, convertToHtml, EmojiItem, emojiMap, emojis } from '@/utils/emoji'
import { isNumber, toInteger, uniqueId } from 'lodash';
import logger from '@/utils/logger';
import { isChildElement, isChildNode } from '@/utils/dom';
const props = defineProps({
  modelValue: {
    type: String,
    required: false,
  },
  autofocus: {
    type: Boolean,
    default: false,
  },
  placeholder: {
    type: String,
    default: ''
  },
  //是否禁止输入
  disabled: {
    type: Boolean,
    default: false,
  },
  //是否可以换行
  breakable: {
    type: Boolean,
    default: true
  }
})
const emits = defineEmits<{
  'update:modelValue': [value: string]
  //当删除元素时
  nodeRemove: [node: Node]
  //按shift+enter来触发提交事件
  submit: []
}>()
//已输入的内容(和html内容不同)
const text = ref('')
const length = computed(()=>text.value.length)
//输入的div元素
const inputRef = ref<HTMLDivElement>()
//外部改变内容时
watch(()=>props.modelValue, value=>{
  if (value !== undefined && value !== text.value) {
    parse(value)
  }
}, { immediate: true })
const observer = new MutationObserver((mutationList, observer)=>{
  for (const mutation of mutationList) {
    if (mutation.type === 'childList') {
      mutation.removedNodes.forEach(node=>{
        emits('nodeRemove', node)
      })
    }
  }
})
onMounted(() => {
  observer.observe(inputRef.value, { childList: true })
  //插入默认值
  if (props.modelValue) {
    insertNode(new Text(props.modelValue))
  }
})

//插入表情
function insertEmoji(i: EmojiItem) {
  const img = document.createElement('img')
  img.src = i.url
  img.alt = i.name
  img.classList.add('emoji')
  // img.contentEditable = 'false' //默认是inherit,也就是true
  img.draggable = false
  img.style.pointerEvents ='none' //方便在表情和表情之间插入光标
  img.dataset.size = emojiMap.find(v=>v.emojis.some(a=>a.id===i.id)).size+''
  insertNode(img)
}

//在光标处插入node
//如果点击了支持user-select的元素可能会影响光标位置
function insertNode(node: Node) {
  const selection = window.getSelection()
  //准备编辑的光标对象
  let range : Range = null
  //如果有记录,先设置位置再插入
  if (_range && document.activeElement !== inputRef.value) {
    range = _range
    // selection.getRangeAt(0).setStart(range.startContainer, _range.startOffset)
  } else {
    // if (selection.rangeCount < 1) {
      //如果找不到光标则需要先获取焦点
      inputRef.value.focus()
    // }
    range = selection.getRangeAt(0)
  }

  //防止插到input以外的地方
  if (range.startContainer != inputRef.value && !isChildNode(range.startContainer, inputRef.value)) {
    return logger.debug('input以外的位置', range)
  }
  
  //在光标处插入对象
  range.insertNode(node)
  //取消选择(光标选中的高亮)
  range.collapse(false)
  //设置光标位置在插入对象之后
  range.setStartAfter(node)
  //更新内容
  updateContent()
  // range.startOffset
  // const fontSize = getComputedStyle(inputRef.value).fontSize.replace('px', '')
  // const lineHeight = parseFloat(fontSize) * 1.2
  // const top = Math.max(range.startOffset * lineHeight - inputRef.value.clientHeight, 0)
  // scrollbar.value.scrollTo(0, top)
  // console.log('滚动' + range.startOffset * lineHeight);
}
let _range : Range = null
//失去焦点前记录光标位置,方便插入其它元素
function onBlur(e: FocusEvent) {
  const selection = window.getSelection()
  if (selection.rangeCount > 0) {
    const range = selection.getRangeAt(0)
    _range = range
  }
}
//输入前事件(中文输入无法取消)
//所以div无法限制最大输入长度
function onBeforeInput(e: InputEvent) {
  // console.log(e.cancelable); false
  if (e.dataTransfer) {
    // e.dataTransfer.clearData() 没用
    e.preventDefault()
  }
  //默认在换行时,换行符会变成: <br>, 输入值以后变为: <div>新一行的内容</div>
  //注意阻止默认行为后,换行时,不会自动滚动到光标处
  else if (e.inputType == 'insertLineBreak' || e.inputType == 'insertParagraph') {
    if (!props.breakable) {
      e.preventDefault()
    }
    //插入文本节点来代替<br>
    //在段落末尾插入 \n 无法完成换行
    // let node = document.createTextNode('\n')
    // if (diff > node.length) {
    //   insertNode(node)
    // }
  }
}
//输入后事件
function onInput(e: InputEvent) {
  updateContent()
}
//复制其它网站,软件的文字时,默认会将样式复制进来
//dataTransfer会将复制的内容解析成了很多种类型 text/html, text/plain ...
//根据getData的类型不同,结果也不同
function onPaste(e: ClipboardEvent) {
  e.preventDefault()
  //获取纯文本格式的内容
  let pastedData = e.clipboardData.getData('text/plain')
  if (!props.breakable) {
    pastedData = pastedData.replaceAll('\n', ' ')
  }
  document.execCommand('insertText', false, pastedData)
  // insertNode(new Text(pastedData))
  logger.debug('粘贴: ', pastedData)
}
//将光标移动到末尾
function cursorToEnd() {
  const selection = document.getSelection()
  const range = document.createRange()
  selection.removeAllRanges()
  range.selectNodeContents(inputRef.value)
  range.collapse(false)
  selection.addRange(range)
}
//提取<img>的alt值
const reg = /<img((?!alt).)*alt="([^"]+)"[^>]*>/g
const spanReg = /<span[^<>]*>([^<>]+)<\/span>/g
const divReg = /<div.*?>(.*?)<\/div>/g
function updateContent() {
  let html = inputRef.value.innerHTML
  //将innerHTML的所有[表情]转为img标签
  for (const title of emojis.filter(a=>a.type!==4).map(v=>v.name)) {
    const raw = title.replaceAll(/\[|\]/g, '')
    const r = new RegExp('(?<!alt=")\\['+raw+'\\]')
    while (r.test(html)) {
      const id = uniqueId()
      html = html.replace(r, convertEmoji(title, id))
      inputRef.value.innerHTML = html
      if (window.getSelection().rangeCount > 0) {
        //遍历所有子节点寻找刚刚添加的表情
        inputRef.value.childNodes.forEach(a=>{
          if (a instanceof HTMLImageElement && a.dataset.id == id) {
            //设置光标在刚添加的表情之后
            window.getSelection().getRangeAt(0).setStartAfter(a)
          }
        })
      }
    }
  }
  //将所有img标签改为[alt]的内容
  html = html.replaceAll(reg, '$2')
  //将所有span标签改为原始内容
  html = html.replaceAll(spanReg, '$1')
  //将换行导致的div标签替换为实际的内容
  html = html.replaceAll(divReg, '\n$1')
    .replaceAll('<br>', '')
  // console.log(html);
  text.value = html
  emits('update:modelValue', text.value)
}
//按回车键时处理换行还是提交
function onKeyDown(e: KeyboardEvent) {
  if (e.key == 'Enter' && !props.disabled) {
    if (e.shiftKey) {
      //必须阻止默认事件,否则enter会算作输入一个换行符
      e.preventDefault()
      emits('submit')
      return
    }
  }
}

//解析指定内容到页面上
function parse(str: string) {
  if (inputRef.value==null) return ''
  str = convertToHtml(str)
  inputRef.value.innerHTML = str
  updateContent()
}

//清空内容
function clear() {
  if (inputRef.value) {
    inputRef.value.innerHTML = ''
  }
  text.value = ''
}

defineExpose({
  inputRef,
  text,
  length,
  insertNode,
  insertEmoji,
  clear,
  focus() {
    inputRef.value?.focus()
  },
  blur() {
    inputRef.value?.blur()
  }
})
</script>

<style scoped lang="scss">
.rich-input {
  transition: all .3s;
  outline: none;
  overflow-x: hidden;
  overflow-y: auto;
  /* padding-bottom: 6px; //防border */
  /* padding-right: 17px; //防止和滚动条重叠 */
  white-space: pre-wrap; //div必须设置后才会换行
  word-wrap: break-word;
  word-break: break-all;
  &[is-empty='true']::before {
    content: attr(placeholder);
    color: var(--grey2);
    font-size: inherit;
    pointer-events: none;
    position: absolute;
  }
  /* contenteditable=false 高度会变为0 */
  &.disabled::before {
    position: static;
  }
  /* //div内可能出现<br>导致判断未空
  &:empty::before {
    content: attr(placeholder);
    color: var(--grey2);
    font-size: 14px;
    pointer-events: none;
  } */
  img.emoji {
    margin: 0 2px; //方便鼠标插入光标
  }
  &.disabled {
    pointer-events: none;
    user-select: none;
  }
}
</style>