<template>
  <el-dialog v-model="visible" width="500px">
    <div class="flex gap-4 mb-6">
      <miku-image :res-id="bangumi?.posterId" poster preview class="w-[100px]" />
      <div class="flex flex-col pb-2">
        <div class="font-bold text-xl py-2">{{ title }}</div>
        <div class="mt-auto">请发表您对该作品最客观的评价</div>
        <el-rate v-model="form.rate" :max="10" size="large" show-score/>
      </div>
    </div>
    <el-input
      placeholder="请输入评价内容"
      v-model="form.content" 
      :rows="5"
      type="textarea" 
      resize="none" 
      maxlength="800" 
      show-word-limit
    />
    <div class="flex justify-center mt-4">
      <el-button class="mx-auto" type="primary" @click="submit" :disabled="!form.rate">发表评价</el-button>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import bangumiApi from '@/apis/user/rate';
import { useAsyncFn } from '@/hooks/useAsyncFn';
const visible = ref(true)
const props = defineProps<{ bangumi: Bangumi, title: string }>()
const emits = defineEmits<{ submit: [data: UserRate] }>()
const form = reactive<UserRateDto>({
  targetId: props.bangumi.id,
  rate: props.bangumi.userRate?.rate ?? 0,
  content: props.bangumi.userRate?.content ?? ''
})
const submit = useAsyncFn(async ()=>{
  const data = await bangumiApi.rate(form)
  props.bangumi.userRate = data
  message.success('发表成功')
  visible.value = false
  emits('submit', data)
})
</script>

<style scoped lang="scss">
.el-rate {
  --el-rate-icon-size: 24px;
}
</style>