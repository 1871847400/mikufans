<template>
  <div class="bg0 p-4 shadow rounded-lg">
    <el-form class="search-bar shadow" label-suffix=":" @submit.prevent="commit">
      <el-form-item label="请求方式">
        <el-select v-model="queries.reqMethod" clearable>
          <el-option label="GET" value="GET" />
          <el-option label="POST" value="POST" />
          <el-option label="PUT" value="PUT" />
          <el-option label="DELETE" value="DELETE" />
        </el-select>
      </el-form-item>
      <el-form-item label="设备类型">
        <el-select v-model="queries.clientType" clearable>
          <el-option label="电脑" value="1" />
          <el-option label="手机" value="2" />
          <el-option label="其它" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="用户类型">
        <el-select v-model="queries.userType" clearable>
          <el-option label="普通" value="0" />
          <el-option label="管理员" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="search" native-type="submit" type="primary">搜索</el-button>
      </el-form-item>
      <el-form-item>
        <el-button icon="refresh" @click="reset(true)">重置</el-button>
      </el-form-item>
    </el-form>
    <action-bar 
      :selections="selections" 
      :remove="remove" 
    />
    <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row show-overflow-tooltip>
      <el-table-column type="selection" width="55" />
      <el-table-column prop="title" label="操作" width="200" />
      <el-table-column prop="username" label="用户名"  width="200" />
      <el-table-column prop="reqUri" label="请求路径"  width="200" />
      <el-table-column prop="reqMethod" label="请求方式"  width="100" />
      <el-table-column prop="ipaddr" label="IP"  width="120" />
      <el-table-column label="状态" width="80" v-slot="{ row }">
        <el-tag :type="row.operStatus==0?'success':'danger'">{{ row.operStatus==0?'成功':'失败'}}</el-tag>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="200" />
      <el-table-column  label="操作" v-slot="{ row }" width="120">
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
const selections = ref<UserOperLog[]>([])
const { page, run, loading } = usePage(()=>pageModel('user_oper_log', queries))
const { queries, reset, commit } = useRouteQueries({
  reqMethod: null,
  clientType: null,
  userType: null,
  pageNum: 1,
}, { autoCommit: ['pageNum'] })
watchImmediate(()=>route.query, ()=>run(1))
async function remove(...models: UserOperLog[]) {
  await message.confirm(`确定删除${ models.map(a=>a.id) }吗?`)
  await removeModel('user_oper_log', models.map(a=>a.id))
  run(1)
}
</script>

<style scoped lang="scss">
</style>