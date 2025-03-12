<template>
  <el-dialog v-model="show" title="请通过验证码" width="530px" center :close-on-click-modal="false" :close-on-press-escape="false">
    <div class="relative overflow-hidden" v-loading="loading">
      <el-image class="captcha-img" :src="puzzleResult.image" fit="fill"/>
      <el-image ref="puzzle" class="captcha-puzzle" :src="puzzleResult.puzzle" fit="fill"/>
      <div v-if="status===1" class="mask" style="color: green">
        <el-result icon="success" title="验证成功"/>
      </div>
      <div v-else-if="status===0" class="mask" style="color: red">
        <el-result icon="error" title="验证失败"/>
      </div>
    </div>
    <div class="slider-input">
      <!-- 必须加载完成拿到拼图id后才能拖动 -->
      <!-- @mousedown="isDragging=true" -->
      <svg-icon ref="piece" name="slider" :size="50" fill="#1a73e8" class="slider-piece"/>
      <div v-if="!isDragging&&x===0" class="abs-center text-lg">请滑动到正确的位置</div>
    </div>
  </el-dialog>
</template>

<script lang="ts" setup>
import captchaApi from '@/apis/security/captcha';
import { sleep } from '@/utils/datetime';
const show = ref(true)
const emits = defineEmits<{
  success: [puzzleId: string]
}>()
const piece = ref()
const el = toRef(()=>piece.value?.$el as HTMLElement)
const containerElement = toRef(()=>piece.value?.$el.parentElement as HTMLElement)
const puzzle = ref()
const status = ref(-1) //-1未操作 0失败 1成功
const loading = ref(true) //加载图片中
const submitting = ref(false) //提交中
const imgX = ref(0) //拼图移动的像素
const finalValue = ref(0) //最终提交的百分比
const { x, isDragging } = useDraggable(el, {
  axis: 'x',
  disabled: computed(()=>submitting.value||loading.value),
  containerElement,
  onMove(position) {
    //滑块移动的百分比
    const value = position.x / (containerElement.value.clientWidth - el.value.clientWidth)
    const img : HTMLElement = puzzle.value?.$el
    imgX.value = img ? value * (img.parentElement.clientWidth - img.clientWidth) : 0
    finalValue.value = imgX.value / img.parentElement.clientWidth
  },
  async onEnd() {
    try {
      submitting.value = true
      try {
        await captchaApi.validate(puzzleResult.puzzleId, finalValue.value)
        status.value = 1
      } catch {
        status.value = 0
      }
      await sleep(2000)
      //滑块回到起点
      x.value = 0
      if (status.value===1) {
        //如果成功关闭dialog
        emits('success', puzzleResult.puzzleId)
        show.value = false
      } else {
        //如果失败,刷新验证码
        updatePuzzle()
      }
    } finally {
      submitting.value = false
    }
  }
})
const puzzleResult = reactive<PuzzleResult>({
  puzzle: '',
  image: '',
  offset: 0,
  puzzleId: '',
})
async function updatePuzzle() {
  loading.value = true
  const data = await captchaApi.generate()
  Object.assign(puzzleResult, data)
  status.value = -1
  loading.value = false
}
updatePuzzle()
</script>

<style lang="scss" scoped>
.mask {
  width: 100%;
  height: 100%;
  position: absolute;
  left: 0;
  top: 0;
  background-color: rgba(255,255,255,.8);
  font-size: 18px;
  z-index: 1;
  user-select: none;
  pointer-events: none;
  display: flex;
  justify-content: center;
  align-items: center;
}
.captcha-puzzle {
  width: 13.33%; // 拼图宽高64 64/480   16 / 9
  position: absolute;
  top: v-bind('puzzleResult.offset * 100 + "%"');
  left: v-bind('imgX + "px"');
  transform: translateY(-1px);
  pointer-events: none;
  user-select: none;
}
.slider-input {
  margin-top: 20px;
  margin-bottom: 16px;
  width: 100%;
  height: 32px;
  position: relative;
  background-color: #e4e3e3;
  border-radius: 20px;
  user-select: none;
  .slider-piece {
    cursor: pointer;
    position: absolute;
    top: 50%;
    left: v-bind('x + "px"');
    transform: translateY(-50%);
    z-index: 999;
  }
}
//原始图像必须保持原始比例 16/9
//理想原图为: 480:270
.captcha-img {
  width: 100%;
  aspect-ratio: 16/9;
  margin: 0 auto;
  pointer-events: none;
  user-select: none;
}
</style>