/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{html,js,ts,vue}'],
  theme: {
    //注意覆盖需要复写所有选项,不会与旧项合并
    fontSize: {
      'xs': ['12px', 1.25],
      '13': ['13px', 1.25],
      'sm': ['14px', 1.25],
      '15': ['15px', 1.25],
      'base': ['16px', 1.25],
      'lg': ['18px', '28px'],
      'xl': ['20px', '36px'],
      '2xl': ['24px', '35px'],
    },
    // @media (max-width: 1535px) { ... }
    screens: {
      '2xl': {'max': '1535px'},
      'xl': {'max': '1279px'},
      'lg': {'max': '1023px'},
      'md': {'max': '767px'},
      'sm': {'max': '639px'},
    },
    //这里是扩展项,不会覆盖原项
    extend: {
      aspectRatio: {
        //这里value不能填数字
      },
      //gap
      spacing: {
        1.5: '0.375rem'
      },
      //用于类似于视频标题,让高度固定,以免内容不够撑开高度
      //需要对应上方fontSize里的行高
      height: {
        'xs-2': '30px',
        'xs-3': '45px',
        'xs-4': '60px',
        'xs-5': '65px',
        'sm-2': '35px',
        'sm-3': '52.5px',
        'base-2': '40px',
      }
    },
  },
  plugins: [],
  corePlugins: {
    preflight: false, //关闭默认样式
  }
}