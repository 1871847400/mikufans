<template>
  <div>
    <div class="bg0 p-4 rounded-lg shadow" >
      <action-bar 
        :selections="selections"
        :create="editChannel"
        :update="editChannel"
        :remove="removeChannel"
      />
      <el-table v-loading="isLoading" row-key="id" :fit="false" :data="channels" @selection-change="selections=$event" stripe highlight-current-row show-overflow-tooltip>
        <el-table-column type="selection" width="50"/>
        <el-table-column label="分区名称" prop="channelName" width="150"/>
        <el-table-column label="分区描述" prop="channelDesc" width="200"/>
        <el-table-column label="图标名称" prop="iconName" width="200"/>
        <el-table-column label="跳转链接" prop="url" width="200"/>
        <el-table-column label="创建人" prop="createByName" width="100"/>
        <el-table-column label="创建时间" prop="createTime" width="180"/>
        <el-table-column label="状态" width="80" v-slot="{ row }">
          <el-switch v-model="row.disabled" @change="changeStatus(row)" :active-value="0" :inactive-value="1" active-text="正常" inactive-text="停用" inline-prompt />
        </el-table-column>
        <el-table-column label="操作" width="150" v-slot="{ row }">
          <el-button icon="edit" type="success" @click="editChannel(row)"/>
          <el-button icon="delete" type="danger" @click="removeChannel(row)"/>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import { listModel, pageModel, removeModel, updateModel } from '@/apis/admin/model';
import { sortBy } from 'lodash';
import { openDialog } from '@/utils/dialog';
import AppFormDialog from '@/dialogs/AppFormDialog.vue';
const selections = ref<VideoChannel[]>([])
const table = 'video_channel'
const { state, isLoading, execute } = useAsyncState<VideoChannel[]>(()=>listModel(table), [])
const channels = computed(()=>{
  let result = [...state.value]
  for(const channel of state.value) {
    if (channel.pid !== '0') {
      const parent = result.find(a=>a.id===channel.pid)
      if (parent.children) {
        parent.children.push(channel)
      } else {
        parent.children = [channel]
      }
      parent.children = sortBy(parent.children, ['sort'])
      result = result.filter(a=>a.id!==channel.id)
    }
  }
  return sortBy(result, ['sort'])
})
function editChannel(model?: VideoChannel) {
  openDialog(AppFormDialog, {
    model,
    table,
    onSubmit: execute,
  })
}
async function removeChannel(...videoChannel: VideoChannel[]) {
  await message.confirm(`确认删除${videoChannel.map(a=>a.channelName)}吗`)
  await removeModel(table, videoChannel.map(a=>a.id))
  execute()
}
async function changeStatus(row: VideoChannel) {
  await updateModel(table, { 
    id: row.id, 
    disabled: row.disabled 
  })
  message.success('更新成功')
}
</script>

<style scoped lang="scss">
</style>