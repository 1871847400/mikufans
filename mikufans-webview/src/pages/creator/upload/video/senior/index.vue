<template>
  <el-form-item label="评论设置">
    <el-radio-group v-model="dynamic.commentArea.commentFlag">
      <el-radio label="开启评论" value="NORMAL" title="任何人都可以评论" />
      <el-radio label="精选评论" value="CHOICE" title="UP主选中的评论才会显示" />
      <el-radio label="关闭评论" value="DISABLED" title="任何人都无法评论" />
    </el-radio-group>
  </el-form-item>
  <el-form-item label="可见范围">
    <el-radio-group v-model="dynamic.visible">
      <el-radio label="公开可见" :value="1" title="可被任何人搜索到" />
      <el-radio label="仅自己可见" :value="0" title="其它人无法搜索和观看" />
    </el-radio-group>
  </el-form-item>
  <el-form-item label="观看等级限制">
    <el-switch v-model="enableLevel" class="mr-4" @change="onChange"/>
    <el-radio-group v-model="data.userLevel" v-if="enableLevel">
      <template v-for="i in 6">
        <el-radio 
          :label="'lv.'+i" 
          :value="i" 
          :disabled="userStore.level<i"
          :title="userStore.level<i?`您需要达到${i}级才能解锁`:`低于${i}级的无法观看`"
        />
      </template>
    </el-radio-group>
  </el-form-item>
  <publish-time v-model="dynamic" v-if="!oldData?.search && !oldData?.disabled" />
</template>

<script setup lang="ts">
import PublishTime from './PublishTime.vue';
const userStore = useUserStore()
const { data, oldData } = toRefs(useUploadStore())
const { dynamic } = toRefs(data.value)
const enableLevel = ref(data.value.userLevel > 0)
function onChange(val: boolean) {
  if (val === false) {
    data.value.userLevel = 0
  }
}
</script>

<style scoped lang="scss">
</style>