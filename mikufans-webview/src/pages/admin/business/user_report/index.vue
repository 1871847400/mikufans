<template>
  <div>
    <el-form class="search-bar shadow" label-suffix=":" @submit.prevent="commit">
      <el-form-item label="举报类型">
        <el-select v-model="queries.reportType">
          <el-option label="视频" value="VIDEO" />
          <el-option label="评论" value="COMMENT" />
          <el-option label="弹幕" value="DANMU" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="search" native-type="submit" type="primary">搜索</el-button>
      </el-form-item>
      <el-form-item>
        <el-button icon="refresh" @click="reset(true)">重置</el-button>
      </el-form-item>
    </el-form>
    <div class="bg0 p-4 rounded-lg shadow" >
      <action-bar 
        :selections="selections"
      />
      <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row show-overflow-tooltip>
        <el-table-column type="selection" width="50"/>
        <el-table-column label="举报类型" prop="target.business" width="80"/>
        <el-table-column label="举报内容" width="300" v-slot="{ row }">
          <a class="hoverable" title="访问业务源" :href="row.target.uri" target="_blank">{{ row.target.message }}</a>
        </el-table-column>
        <el-table-column label="举报次数" prop="reportCount" width="100"/>
        <el-table-column label="操作" width="300" v-slot="{ row }">
          <el-button icon="Search" type="default">
            <a :href="row.target.uri" target="_blank">查看</a>
          </el-button>
          <el-button icon="List" type="success" @click="list(row)">详情</el-button>
          <el-button icon="Checked" type="primary" @click="audit(row)">审核</el-button>
        </el-table-column>
      </el-table>
      <miku-pagination v-model="queries.pageNum" :page="page" />
    </div>
  </div>
</template>

<script setup lang="ts">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import { openDialog } from '@/utils/dialog';
import { usePage } from '@/hooks/usePage';
import userReportApi from '@/apis/admin/report'
import UserReportDialog from './UserReportDialog.vue';
import { useRouteQueries } from '@/utils/route';
import ReportListDialog from './ReportListDialog.vue';
const selections = ref<UserReport[]>([])
const route = useRoute()
const { queries, commit, reset } = useRouteQueries({
  reportType: null,
  pageNum: 1,
}, { autoCommit: ['pageNum'] })
const { page, loading, run } = usePage(search)
watchImmediate(()=>route.query, ()=>run(queries.pageNum), { deep: true })
function search(pageNum: number) {
  return userReportApi.search({
    ...queries,
    pageNum,
    pageSize: 10
  })
}
function audit(row: UserReportVo) {
  openDialog(UserReportDialog, {
    userReport: row,
    onSubmit() {
      run(queries.pageNum)
    }
  })
}
function list(row: UserReportVo) {
  openDialog(ReportListDialog, {
    targetId: row.targetId,
  })
}
</script>

<style scoped lang="scss">
</style>