<template>
  <div>
    <el-rate v-model="rate.rate" disabled :min="1" :max="10" size="large" show-score/>
    <rich-text class="text-sm mb-2" :content="rate.content" :rows="3" :html="false"/>
    <bangumi-intro :bangumi="rate.bangumi" />
  </div>
</template>

<script setup lang="ts">
import BangumiIntro from '@/pages/video/left/intro/BangumiIntro.vue';
const props = defineProps<{ data: UserDynamic }>()
const rate = toRef(()=>props.data.source as UserRate)
</script>

<style scoped lang="scss">
.el-rate {
  --el-rate-icon-size: 24px;
}
</style>