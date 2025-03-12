<template>
  <div>
    <div class="bg0 p-4 rounded-lg shadow">
      <el-table v-loading="loading" :fit="false" :data="page?.records" stripe highlight-current-row>
        <el-table-column label="id" prop="id" width="100"/>
        <el-table-column label="用户名" prop="username" width="240"/>
        <el-table-column label="用户类型" prop="userType" width="120"/>
        <el-table-column label="IP" prop="ipaddr" width="200"/>
        <el-table-column label="地点" prop="location" width="100"/>
        <el-table-column label="时间" prop="time" width="200"/>
      </el-table>
      <miku-pagination v-model="pageNum" :page="page" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import { getOnlineUsers } from '@/apis/admin/online';
import { toInteger } from 'lodash';
const pageNum = useRouteQuery('pageNum', 1, { transform: toInteger })
const { page, loading, run } = usePage((pageNum)=>getOnlineUsers({ pageNum }))
watch(pageNum, ()=>run(pageNum.value), { immediate: true, deep: true })
</script>

<style scoped lang="scss">
</style>