<template>
  <Async :loading="isLoading" :error="error" min-h="400px">
    <el-form label-suffix=":" label-position="left" label-width="200px">
      <div class="flex gap-8 m-4 *:flex-1">
        <el-card header="CPU">
          <el-form-item label="核心数">{{ state.cpu.cpuNum }}</el-form-item>
          <el-form-item label="系统使用率">{{ state.cpu.sys }}</el-form-item>
          <el-form-item label="用户使用率">{{ state.cpu.used }}</el-form-item>
          <el-form-item label="当前等待率">{{ state.cpu.wait }}</el-form-item>
          <el-form-item label="当前空闲率">{{ state.cpu.free }}</el-form-item>
        </el-card>
        <el-card header="内存">
          <el-form-item label="内存总量">{{ state.mem.total }}</el-form-item>
          <el-form-item label="已用内存">{{ state.mem.used }}</el-form-item>
          <el-form-item label="剩余内存">{{ state.mem.free }}</el-form-item>
          <el-form-item label="使用率">{{ state.mem.usage }}</el-form-item>
        </el-card>
      </div>
      <div class="flex gap-8 m-4 *:flex-1">
        <el-card header="系统">
          <el-form-item label="主机名称">{{ state.sys.hostName }}</el-form-item>
          <el-form-item label="主机地址">{{ state.sys.address }}</el-form-item>
          <el-form-item label="系统名称">{{ state.sys.osName }}</el-form-item>
          <el-form-item label="系统版本">{{ state.sys.osVersion }}</el-form-item>
          <el-form-item label="系统架构">{{ state.sys.osArch }}</el-form-item>
          <el-form-item label="系统用户">{{ state.sys.userName }}</el-form-item>
          <el-form-item label="运行目录">{{ state.sys.userDir }}</el-form-item>
          <el-form-item label="用户时区">{{ state.sys.userTimezone }}</el-form-item>
        </el-card>
        <el-card header="Jvm">
          <el-form-item label="安装目录">{{ state.jvm.home }}</el-form-item>
          <el-form-item label="版本">{{ state.jvm.version }}</el-form-item>
          <el-form-item label="发行商">{{ state.jvm.vendor }}</el-form-item>
          <el-form-item label="内存总量">{{ state.jvm.total }}</el-form-item>
          <el-form-item label="最大内存">{{ state.jvm.max }}</el-form-item>
          <el-form-item label="空闲内存">{{ state.jvm.free }}</el-form-item>
          <el-form-item label="启动时间">{{ state.jvm.startTime }}</el-form-item>
          <el-form-item label="启动时长">{{ state.jvm.runTime }}</el-form-item>
          <el-form-item label="运行参数">
            <rich-text :content="state.jvm.inputArgs" :rows="3" :line-height="2" />
          </el-form-item>
        </el-card>
      </div>
      <el-card class="m-4" header="磁盘">
        <el-table :data="state.disk.sysFiles" stripe highlight-current-row>
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="type" label="类型" />
          <el-table-column prop="mount" label="挂载点" />
          <el-table-column prop="total" label="总空间" />
          <el-table-column prop="free" label="空闲" />
          <el-table-column prop="used" label="已使用" />
          <el-table-column prop="usage" label="使用率" />
        </el-table>
      </el-card>
    </el-form>
  </Async>
</template>

<script setup lang="ts">
import { getServerInfo } from '@/apis/admin/server';
const { state, isLoading, error } = useAsyncState(getServerInfo(), {} as ServerInfo)
</script>

<style scoped lang="scss">
.el-card {
}
.el-form-item {
  :deep(.el-form-item__content) {
    word-wrap: break-word;
    word-break: break-all;
  }
}
</style>