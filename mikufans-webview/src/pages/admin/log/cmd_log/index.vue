<template>
  <div class="bg0 p-4 shadow rounded-lg">
    <action-bar 
      :selections="selections" 
      :remove="remove" 
    />
    <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row show-overflow-tooltip>
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="id" width="60" />
      <el-table-column prop="cmd" label="命令" width="320" />
      <el-table-column prop="output" label="输出日志"  width="200" />
      <el-table-column prop="errorMsg" label="错误信息"  width="200" />
      <el-table-column prop="exitCode" label="退出码" width="80" />
      <el-table-column prop="createTime" label="创建时间" width="200" />
      <el-table-column  label="操作" v-slot="{ row }" width="200">
        <el-button icon="delete" type="danger" @click="remove(row)">删除</el-button>
      </el-table-column>
    </el-table>
    <miku-pagination v-model="queries.pageNum" :page="page" />
  </div>
</template>

<script setup lang="ts">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import { createModel, pageModel, removeModel } from '@/apis/admin/model';
import { usePage } from '@/hooks/usePage';
import { useRouteQueries } from '@/utils/route';
const route = useRoute()
const selections = ref<CmdLog[]>([])
const { page, run, loading } = usePage(()=>pageModel('cmd_log', queries))
const { queries, reset } = useRouteQueries({
  pageNum: 1
})
watchImmediate(()=>route.query, ()=>run(1))
async function remove(...models: CmdLog[]) {
  await message.confirm(`确定删除${ models.map(a=>a.id) }吗?`)
  await removeModel('cmd_log', models.map(a=>a.id))
  run(1)
}
</script>

<style scoped lang="scss">
</style>