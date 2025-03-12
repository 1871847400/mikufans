<template>
  <div>
    <el-form class="search-bar shadow" label-suffix=":" @submit.prevent="commit">
      <el-form-item label="id">
        <el-input v-model.trim="queries.id" placeholder="请输入用户id" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model.trim="queries.username" placeholder="请输入用户邮箱" />
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model.trim="queries.nickname" placeholder="请输入用户昵称" />
      </el-form-item>
      <el-form-item label="等级">
        <el-input v-model.trim.number="queries.level" placeholder="请输入用户等级" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model.trim="queries.disabled" placeholder="请选择状态">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
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
        :create="editUser"
        :update="editUser"
      />
      <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row>
        <el-table-column type="selection" width="50"/>
        <el-table-column label="id" prop="id" width="100"/>
        <el-table-column label="邮箱" prop="username" width="200"/>
        <el-table-column label="昵称" prop="nickname" width="200"/>
        <el-table-column label="性别" width="80" v-slot="{ row }">
          <dict-tag type="user_gender" :value="row.gender" />
        </el-table-column>
        <el-table-column label="等级" prop="level" width="80" v-slot="{ row }">
          <svg-icon :name="'level_'+row.level" :size="40"/>
        </el-table-column>
        <el-table-column label="硬币" prop="coin" width="120"/>
        <el-table-column label="状态" width="100">
          <template v-slot="{ row }">
            <el-switch v-model="row.disabled" @change="changeStatus(row)" :active-value="0" :inactive-value="1" active-text="正常" inactive-text="停用" inline-prompt />
          </template>
        </el-table-column>
        <el-table-column label="注册日期" prop="registerTime" width="120"/>
        <el-table-column label="操作" width="150">
          <template v-slot="{ row }">
            <el-button icon="edit" type="success" @click="editUser(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
      <miku-pagination v-model="queries.pageNum" :page="page" />
    </div>
  </div>
</template>

<script setup lang="ts">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import { pageModel, updateModel } from '@/apis/admin/model';
import { usePage } from '@/hooks/usePage';
import { useRouteQueries } from '@/utils/route';
import { openDialog } from '@/utils/dialog';
import AppFormDialog from '@/dialogs/AppFormDialog.vue';
const selections = ref<User[]>([])
const route = useRoute()
const { queries, commit, reset } = useRouteQueries({
  id: null,
  username: null,
  nickname: null,
  level: null,
  disabled: null,
  pageNum: 1
}, { autoCommit: ['pageNum'] })
const { page, loading, run } = usePage(()=>pageModel('user', queries))
watch(()=>route.query, ()=>run(queries.pageNum), { immediate: true, deep: true })
function editUser(user?: User) {
  openDialog(AppFormDialog, { 
    table: 'user',
    model: user,
    onSubmit() {
      run(queries.pageNum)
    }
  })
}
async function changeStatus(user: User) {
  await updateModel('user', {
    id: user.id,
    disabled: user.disabled
  })
  message.success('更新成功')
}
</script>

<style scoped lang="scss">
</style>