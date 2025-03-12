<template>
  <div>
    <div class="text-center text-xl">扫描二维码登录</div>
    <div class="qrcode">
      <canvas ref="qrcode" width="160" height="160"></canvas>
    </div>
    <div class="text-center">请使用<a class="blue0" href="#">手机APP</a></div>
    <div class="text-center">扫码登录或扫码下载APP</div>
  </div>
</template>

<script setup lang="ts">
import { baseURL } from '@/apis/service';
import QRCode from 'qrcode';
const qrcode = useTemplateRef('qrcode')
onMounted(()=>{
  QRCode.toCanvas(qrcode.value, baseURL, {
    width: 160, //和canvas一致
    errorCorrectionLevel: 'H',
    margin: 1
  })
})
</script>

<style scoped lang="scss">
.qrcode {
  border-radius: 4px;
  border: 1px solid #000;
  padding: 4px;
  margin: 16px 0;
  pointer-events: none;
}
</style>