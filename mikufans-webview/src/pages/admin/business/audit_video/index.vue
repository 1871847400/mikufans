<template>
  <div>
    <div class="bg0 p-4 rounded-lg shadow">
      <video-audit v-if="videoId" :videoId="videoId+''" @close="videoId=''" />
      <template v-else>
        <el-table v-loading="loading" :fit="false" :data="page?.records" stripe highlight-current-row>
          <el-table-column label="#" width="50" v-slot="{ $index }">
            {{ (page.current-1)*page.size+$index+1 }}
          </el-table-column>
          <el-table-column label="SID" prop="sid" width="80"/>
          <el-table-column label="标题" prop="title" width="200"/>
          <el-table-column label="等待审核" prop="auditCount" width="100"/>
          <el-table-column label="是否转载" width="100" v-slot="{ row }">
            <el-tag v-if="row.republish" type="success">是</el-tag>
            <el-tag v-else type="info">否</el-tag>
          </el-table-column>
          <el-table-column label="是否禁用" width="100" v-slot="{ row }">
            <el-tag v-if="row.disabled" type="danger">是</el-tag>
            <el-tag v-else type="info">否</el-tag>
          </el-table-column>
          <el-table-column label="UP主" prop="user.nickname" width="200" />
          <el-table-column label="创建时间" prop="createTime" width="170"/>
          <el-table-column label="操作" width="140" v-slot="{ row }">
            <el-button icon="Checked" type="success" @click="videoId=row.id">审核</el-button>
          </el-table-column>
        </el-table>
        <miku-pagination v-model="pageNum" :page="page" />
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import auditApi from '@/apis/admin/audit'
import VideoAudit from './VideoAudit.vue';
const pageNum = useRouteQuery('pageNum', 1)
const videoId = useRouteQuery('vid')
const { page, loading, run } = usePage(search, { immediate: [1] })
watch(pageNum, run)
function search(pageNum: number) {
  return auditApi.searchVideo({
    pageNum,
    pageSize: 10
  })
}
</script>

<style scoped lang="scss">
</style>