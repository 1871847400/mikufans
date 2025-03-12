<template>
  <div class="bg0 p-2 mb-4 flex gap-4 shadow rounded-md">
    <el-form-item label="类型">
      <el-select v-model="queries.videoType" @change="queries.pageNum=1">
        <el-option label="番剧" value="ANIME" />
        <el-option label="电影" value="MOVIE" />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-input v-model.trim="form.styleName" @keydown.enter="createStyle" placeholder="请输入风格名称" class="mr-1"/>
      <el-button icon="plus" type="primary" @click="createStyle" :disabled="!form.styleName">添加风格</el-button>
    </el-form-item>
  </div>
  <div class="bg0 p-4 shadow rounded-lg">
    <action-bar 
      :selections="selections" 
      :remove="removeStyle" 
    />
    <el-table v-loading="loading" :fit="false" :data="page?.records" row-key="id" @selection-change="selections=$event" stripe highlight-current-row show-overflow-tooltip>
      <el-table-column type="selection" width="50" />
      <el-table-column prop="id" label="id" width="100" />
      <el-table-column prop="styleName" label="风格名称" width="200">
        <template v-slot="{ row }">
          <input-text v-model="row.styleName" @change="changeName(row)" />
        </template>
      </el-table-column>
      <el-table-column prop="createByName" label="创建人" width="120" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column prop="remark" label="备注" width="200" />
      <el-table-column label="操作" v-slot="{ row, $index }" width="150">
        <el-button type="danger" icon="delete" @click="removeStyle(row)">删除</el-button>
      </el-table-column>
    </el-table>
    <miku-pagination v-model="queries.pageNum" :page="page" />
  </div>
</template>

<script setup lang="ts">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import { createModel, pageModel, removeModel, updateModel } from '@/apis/admin/model';
import JsonDialog from '@/dialogs/JsonDialog.vue';
import { usePage } from '@/hooks/usePage';
import { openDialog } from '@/utils/dialog';
import { useRouteQueries } from '@/utils/route';
const route = useRoute()
const { page, run, loading } = usePage(()=>pageModel('bangumi_style', queries))
const { queries, reset } = useRouteQueries({
  videoType: 'ANIME',
  pageNum: 1,
})
watchImmediate(()=>route.query, ()=>run(1))
const selections = ref<BangumiStyle[]>([])
async function removeStyle(...styles: BangumiStyle[]) {
  if (confirm(`确定删除${styles.map(a=>a.styleName)}吗?`)) {
    await removeModel('bangumi_style', styles.map(a=>a.id))
    run(1)
  }
}
const form = reactive({
  styleName: '',
  videoType: '',
})
async function createStyle() {
  form.videoType = queries.videoType
  await createModel('bangumi_style', form)
  message.success('创建成功')
  queries.pageNum = 1
  run(1)
}
async function changeName(row: BangumiStyle) {
  await updateModel<BangumiStyle>('bangumi_style', {
    id: row.id,
    styleName: row.styleName
  })
  message.success('更新成功')
}
function openFormat() {
  openDialog(JsonDialog, { data: selections.value })
}
</script>

<style scoped lang="scss">
.el-select, .el-input {
  width: 160px;
}
</style>