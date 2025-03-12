<template>
  <el-dialog v-model="dialog" title="举报列表" center width="800px">
    <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row>
      <el-table-column type="selection" width="50"/>
      <el-table-column label="举报行为" prop="behaviorName" width="120"/>
      <el-table-column label="举报原因" prop="reason" width="300"/>
      <el-table-column label="举报人" prop="user.nickname" width="100"/>
      <el-table-column label="举报时间" prop="createTime" width="180"/>
    </el-table>
    <miku-pagination v-model="pageNum" :page="page" @change="run($event)" />
  </el-dialog>
</template>

<script setup lang="ts">
import reportApi from '@/apis/admin/report'
import { usePage } from '@/hooks/usePage';
const props = defineProps<{ targetId: string }>()
const dialog = ref(true)
const pageNum = ref(1)
const selections = ref([])
const { page, loading, run } = usePage(search, { immediate: [1] })
function search(pageNum: number) {
  return reportApi.list(props.targetId, {
    pageNum,
    pageSize: 10,
  })
}
</script>

<style scoped lang="scss">
</style>