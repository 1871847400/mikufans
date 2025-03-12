<template>
  <el-form-item>
    <template #label>
      <div>
        <span class="mr-2">定时发布</span>
        <span v-if="data.publishFlag" class="grey2">(*至少推迟2小时最多15天,实际发布时间实际可能受到审核等原因延迟)</span>
      </div>
    </template>
    <el-switch v-model="data.publishFlag" :active-value="1" :inactive-value="0" class="mr-4" @change="onChange" />
    <template v-if="data.publishFlag">
      <el-date-picker v-model="date" value-format="YYYY-MM-DD" class="mr-2" style="width: 180px;" :clearable="false" />
      <el-time-picker v-model="time" format="HH:mm" value-format="HH:mm:ss" style="width: 150px;" :clearable="false" />
    </template>
  </el-form-item>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
const data = defineModel<UserDynamicDto>('modelValue')
const format = 'YYYY-MM-DD HH:mm:ss'
function onChange(value: boolean) {
  const timestr = data.value.publishTime
  if (value) {
    if (!timestr) {
      data.value.publishTime = dayjs().add(2, 'hour').add(5, 'minute').format(format)
    }
  } else {
    data.value.publishTime = ''
  }
}
const date = computed({
  get() {
    return data.value.publishTime.split(' ')[0]
  },
  set(val) {
    data.value.publishTime = val + ' ' + time.value
  }
})
const time = computed({
  get() {
    return data.value.publishTime.split(' ')[1]
  },
  set(val) {
    data.value.publishTime = date.value + ' ' + val
  }
})
</script>

<style scoped lang="scss">
</style>