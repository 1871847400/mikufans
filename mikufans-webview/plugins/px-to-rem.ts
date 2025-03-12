import pxToRemOrVwPlugin from "vite-plugin-px-rem-vw"

export default pxToRemOrVwPlugin({
  type: 'rem',
  options: {
    //html的字体大小
    rootValue: 16,
    //最小被转换的像素
    minPixelValue: 2,
    //需要转换的属性列表 *代表所有, !代表非
    propList: [
      'padding*', 
      'margin*', 
      '*gap',
      'font-size',
      'line-height',
      'width',
      'max-width',
      'min-width',
      'height',
      'max-height',
      'min-height',
      'translate',
      'transform',
      'border-radius',
      'top',
      'right',
      'bottom',
      'left',
    ],
    //忽略的列表,使用css选择器
    selectorBlackList: [
      'html', 'body'
    ],
    //忽略的规则:正则,函数,路径
    // exclude: ''
  },
})