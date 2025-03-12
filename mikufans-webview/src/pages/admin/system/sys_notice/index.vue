<template>
  <div>
    <div class="bg0 p-4 rounded-lg shadow" >
      <action-bar 
        :selections="selections"
        :create="editNotice"
        :update="editNotice"
        :remove="removeNotice"
      />
      <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row show-overflow-tooltip>
        <el-table-column type="selection" width="50"/>
        <el-table-column label="通知类型" prop="noticeType" width="220"/>
        <el-table-column label="标题" prop="title" width="200"/>
        <el-table-column label="内容模板" prop="template" width="240"/>
        <el-table-column label="默认链接" prop="defUrl" width="100"/>
        <el-table-column label="启用时间" prop="enableTime" width="170"/>
        <el-table-column label="到期时间" prop="expireTime" width="170"/>
        <el-table-column label="操作" width="150">
          <template v-slot="{ row }">
            <el-button icon="edit" type="success" @click="editNotice(row)"/>
            <el-button icon="delete" type="danger" @click="removeNotice(row)"/>
          </template>
        </el-table-column>
      </el-table>
      <miku-pagination v-model="queries.pageNum" :page="page" />
    </div>
  </div>
</template>

<script setup lang="ts">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import { pageModel, removeModel, updateModel } from '@/apis/admin/model';
import { usePage } from '@/hooks/usePage';
import { useRouteQueries } from '@/utils/route';
import { openDialog } from '@/utils/dialog';
import AppFormDialog from '@/dialogs/AppFormDialog.vue';
const selections = ref<SysNotice[]>([])
const route = useRoute()
const { queries, commit, reset } = useRouteQueries({
  pageNum: 1
}, { autoCommit: ['pageNum'] })
const { page, loading, run } = usePage(()=>pageModel('sys_notice', queries))
watch(()=>route.query, ()=>run(queries.pageNum), { immediate: true, deep: true })
function editNotice(model?: SysNotice) {
  openDialog(AppFormDialog, {
    table: 'sys_notice',
    model,
    name: '系统通知',
    onSubmit() {
      run(queries.pageNum)
    }
  })
}
async function removeNotice(...sysNotice: SysNotice[]) {
  await message.confirm(`确认删除${sysNotice.map(a=>a.title)}吗`)
  await removeModel('sys_notice', sysNotice.map(a=>a.id))
  run(queries.pageNum)
}
</script>

<style scoped lang="scss">
</style>