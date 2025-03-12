<template>
  <el-dialog v-model="visible" title="修改可见性" width="400px" center>
    <el-radio-group v-model="value">
      <el-radio :value="1" size="large">公开可见</el-radio>
      <el-radio :value="0" size="large">仅自己可见</el-radio>
    </el-radio-group>
    <template #footer>
      <el-button type="primary" @click="submit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import videoApi from '@/apis/video';
const visible = ref(true)
const { video } = defineProps<{
  video: Video
}>()
const value = ref(video.dynamic.visible)
async function submit() {
  await videoApi.update({
    id: video.id,
    dynamic: {
      id: video.dynamic.id,
      visible: value.value,
      publishFlag: null,
      publishTime: null,
      commentArea: null,
    }
  })
  video.dynamic.visible = value.value
  message.success('保存成功')
  visible.value = false
}
</script>

<style scoped lang="scss">
</style>