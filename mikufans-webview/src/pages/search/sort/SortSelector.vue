<template>
  <div class="flex gap-4">
    <el-radio-group class="sort-buttons" fill="#DFF6FD" text-color="#79bbff" v-model="routeQuery">
      <template v-for="{ label, value } in option.options">
        <el-radio-button v-if="value!='CUSTOM'" :label="label" :value="value" />
      </template>
    </el-radio-group>
    <template v-for="{ value } in option.options">
      <div v-if="value=='CUSTOM'">
        <el-date-picker
          v-model="daterange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD" 
          unlink-panels
          clearable
        />
      </div>
    </template>
    <slot name="append"></slot>
  </div>
</template>

<script setup lang="ts">
const { option } = defineProps<{ option: QueryOption }>()
const routeQuery = useRouteQuery<string>(option.query, option.value)
const daterange = computed({
  get() {
    return routeQuery.value?.split('_')
  },
  set(value: string[]) {
    routeQuery.value = value ? value.join('_') : ''
  }
})
</script>

<style scoped lang="scss">
.sort-buttons {
  column-gap: 12px;
  margin-bottom: 12px;
  :deep(.el-radio-button__inner) {
    border: none;
    border-radius: 4px;
  }
}
</style>