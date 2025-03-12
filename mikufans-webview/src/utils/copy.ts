import logger from "./logger";

export function copyText(text: string) {
  //只支持部分浏览器,且要求https
  const clipboard = navigator.clipboard
  if (clipboard) {
    logger.debug('使用navigator.clipboard复制')
    clipboard.writeText(text)
  } else {
    var isRTL = document.documentElement.getAttribute('dir') === 'rtl';
    var element = document.createElement('textarea');
    // 防止在ios中产生缩放效果
    element.style.fontSize = '12pt';
    // 重置盒模型
    element.style.border = '0';
    element.style.padding = '0';
    element.style.margin = '0';
    // 将元素移到屏幕外
    element.style.position = 'absolute';
    element.style[isRTL ? 'right' : 'left'] = '-9999px';
    // 移动元素到页面底部
    let yPosition = window.pageYOffset || document.documentElement.scrollTop;
    element.style.top = `${yPosition}px`;
    //设置元素只读
    element.setAttribute('readonly', '');
    element.value = text;
    document.body.appendChild(element);
  
    element.select();
    element.setSelectionRange(0, element.value.length);
    document.execCommand('copy');
    element.remove();
  }
}

export function copyImage(blobUrl: string) {
}