<template>
  <div class="danmu-bar">
    <label>
      <svg-icon v-if="danmuSetting.enable" name="danmu-on" :size="26" />
      <svg-icon v-else name="danmu-off" :size="26" />
      <input v-model="danmuSetting.enable" type="checkbox" hidden>
    </label>
    <el-popover trigger="hover" placement="top" width="auto" effect="dark" :teleported="false">
      <danmu-setting />
      <template #reference>
        <label>
          <svg-icon name="danmu-config" :size="26" />
        </label>
      </template>
    </el-popover>
    <div class="danmu-input">
      <el-popover trigger="hover" placement="top" width="auto" effect="dark" :teleported="false">
        <danmu-style />
        <template #reference>
          <span class="danmu-style">
            <i class="iconfont icon-zitiyanse"></i>
          </span>
        </template>
      </el-popover>
      <rich-input
        v-model="danmuInput"
        ref="richRef" 
        :placeholder="userStore.isLogin?'发条弹幕试试吧':'请先登录后再发送弹幕'" 
        :breakable="false" 
        @keydown.enter.prevent="onSubmit" 
        :disabled="!userStore.isLogin"
      />
      <el-popover trigger="hover" placement="top" width="310px" :teleported="false">
        <emoji-selector :inputRef="richRef"/>
        <template #reference>
          <span class="danmu-emoji">
            <i class="iconfont icon-biaoqing1"></i>
          </span>
        </template>
      </el-popover>
      <button class="submit" @click="onSubmit">发送</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { danmuInput, danmuSetting, danmuStyle } from './config'
import DanmuSetting from './DanmuSetting.vue'
import DanmuStyle from './DanmuStyle.vue'
import videoDanmuApi from '@/apis/video/danmu'
const { videoElement, videoPart, danmus } = toRefs(useVideoStore())
const userStore = useUserStore()
const richRef = useTemplateRef('richRef')
async function onSubmit() {
  const text = richRef.value.text.trim()
  danmuInput.value = ''
  richRef.value.clear()
  if (text && videoElement.value && videoPart.value && userStore.isLogin) {
    const danmu = await videoDanmuApi.send({
      content: text,
      danmuType: danmuStyle.type,
      fontColor: danmuStyle.color,
      fontType: 1,
      partId: videoPart.value.id,
      sendTime: videoElement.value.currentTime,
    })
    danmus.value.push(danmu)
  }
}
</script>

<style scoped lang="scss">
.danmu-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  label {
    fill: var(--grey1);
    cursor: pointer;
  }
  .danmu-input {
    background-color: var(--bg1);
    color: var(--grey1);
    font-size: 16px;
    height: 32px;
    border-radius: 6px;
    overflow: hidden;
    display: flex;
    align-items: center;
    .danmu-style {
      font-size: 22px;
      cursor: pointer;
      font-weight: 550;
      padding: 0 8px;
    }
    .rich-input {
      width: 240px;
      font-size: 14px;
      white-space: nowrap;
      word-wrap: normal;
      word-break: keep-all;
    }
    .danmu-emoji {
      cursor: pointer;
      padding: 0 8px;
      font-size: 20px;
    }
    .submit {
      background: none;
      background-color: var(--blue0);
      width: 70px;
      height: 100%;
      color: #fff;
      cursor: pointer;
    }
  }
}
</style>