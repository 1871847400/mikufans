<template>
  <el-form class="search-bar shadow" label-suffix=":" @submit.prevent="commit">
    <el-form-item label="视频id">
      <el-input
        clearable
        v-model.trim="queries.id"
        placeholder="请输入视频id"
        @keyup.enter="commit"
      />
    </el-form-item>
    <el-form-item label="视频sid">
      <el-input
        clearable
        v-model.trim="queries.sid"
        placeholder="请输入视频sid"
        @keyup.enter="commit"
      />
    </el-form-item>
    <el-form-item label="视频标题">
      <el-input
        clearable
        v-model.trim="queries.title"
        placeholder="请输入视频标题"
        @keyup.enter="commit"
      />
    </el-form-item>
    <el-form-item label="用户id">
      <el-input
        clearable
        v-model.trim="queries.userId"
        placeholder="请输入用户id"
        @keyup.enter="commit"
      />
    </el-form-item>
    <el-form-item label="视频类型">
      <el-select
        clearable
        v-model.trim="queries.type"
        placeholder="请选择视频类型"
      >
      <el-option label="视频" value="VIDEO" />
      <el-option label="番剧" value="ANIME" />
      <el-option label="电影" value="MOVIE" />
    </el-select>
    </el-form-item>
    <el-form-item label="搜索类型">
      <el-select
        clearable
        v-model.trim="queries.search"
        placeholder="请选择搜索类型"
      >
        <el-option label="可搜索" value="1" />
        <el-option label="不可搜索" value="0" />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button icon="search" native-type="submit" type="primary">搜索</el-button>
    </el-form-item>
    <el-form-item>
      <el-button icon="refresh" @click="reset(true)">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { useRouteQueries } from '@/utils/route'
const { queries, commit, reset } = useRouteQueries({
  id: '',
  sid: '',
  title: '',
  userId: '',
  pageNum: 1,
  type: '',
  search: ''
}, { autoCommit: false })
</script>

<style scoped lang="scss">
</style>