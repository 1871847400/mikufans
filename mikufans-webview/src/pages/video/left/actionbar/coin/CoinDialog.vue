<template>
  <el-dialog v-model="visible" width="420px">
    <div class="text-center text-base">给UP主投上<em>{{ coin }}</em>枚硬币</div>
    <div class="flex justify-evenly mt-8">
      <template v-for="item in coins">
        <div class="coin-box" :class="{active:coin==item.count}" :data-count="item.count+'硬币'" @click="coin=item.count">
          <div class="img-box">
            <img :src="item.src" alt="">
          </div>
        </div>
      </template>
    </div>
    <div class="my-3 pl-7">
      <el-checkbox v-model="favor" label="同时点赞内容"/>
    </div>
    <div class="flex justify-center">
      <el-button type="primary" @click="submit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import coin1 from '@/assets/images/22-coin-ani.png'
import coin2 from '@/assets/images/33-coin-ani.png'
const props = defineProps<{ payCoin: (count: number)=>any }>()
const visible = ref(true)
const coins = reactive([
  { src: coin1, count: 1, },
  { src: coin2, count: 2, },
])
const coin = ref(1)
const favor = ref(true)
const { like, video } = useVideoStore()
function submit() {
  props.payCoin(coin.value)
  if (favor.value && video.likeStatus.likeVal !== 1) {
    like()
  }
  visible.value = false
}
</script>

<style scoped lang="scss">
em {
  padding: 0 8px;
  font-size: 22px;
  font-weight: 550;
}
$frame: 24; //帧图片包含24帧,根据实际写
$width: 120px; //每帧显示多宽,高度会自适应
@keyframes run {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX($frame * $width * -1 + $width);
  }
}
.coin-box {
  border: 2px dashed var(--grey2);
  border-radius: 6px;
  position: relative;
  padding: 16px;
  .img-box {
    width: $width;
    aspect-ratio: 0.623; //加载图片之前高度为0,所以预先指定高度,但要根据图片比例填
    overflow: hidden;
    img {
      width: $frame * $width; //重新调整图片宽高
      animation: run 2s steps($frame - 1) infinite; //步骤为实际帧数-1
      animation-play-state: paused;
    }
  }
  &::before {
    content: attr(data-count);
    position: absolute;
    left: 8px;
    top: 8px;
  }
  &.active {
    border: 2px solid var(--blue1);
    &::before {
      color: var(--blue1);
    }
    img {
      animation-play-state: running;
    }
  }
  &:hover {
    border-color: var(--blue1);
  }
}
</style>