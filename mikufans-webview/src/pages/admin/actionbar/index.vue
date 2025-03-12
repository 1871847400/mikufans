<template>
  <div class="flex py-2 action-bar">
    <el-button v-if="create" icon="plus" type="primary" @click="create()">新增</el-button>
    <el-button v-if="update" icon="edit" type="success" :disabled="selections.length!=1" @click="update(selections[0])">编辑</el-button>
    <el-button icon="list" type="info" :disabled="selections.length==0" @click="openFormat">预览</el-button>
    <el-button v-if="remove" icon="delete" type="danger" :disabled="selections.length==0" @click="onRemove">删除</el-button>
    <slot name="append"></slot>
  </div>
</template>

<script setup lang="ts">
import { openDialog } from '@/utils/dialog';
import JsonDialog from '@/dialogs/JsonDialog.vue';
const props = defineProps({
  selections: array<object>().def([]),
  create: func(),
  update: func<(model: object)=>void>(),
  remove: func<(...selections: object[])=>void>(),
})
function openFormat() {
  openDialog(JsonDialog, { data: props.selections })
}
function onRemove() {
  props.remove(...props.selections)
}
</script>

<style scoped lang="scss">
.action-bar {
}
</style>