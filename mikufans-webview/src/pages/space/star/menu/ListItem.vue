<template>
  <div ref="el" id="star-list-item" class="star-list-item" @click="selectStar=value" :active="selectStar?.id===value.id">
    <i class="iconfont drag-ico">&#xe60c;</i>
    <i class="iconfont mr-2" :class="folderIcon"></i>
    <div class="truncate max-w-[110px]">{{ value.starName }}</div>
    <div class="tip" @click.stop>
      <span class="count">{{ value.starCount }}</span>
      <el-dropdown v-if="isSelf" class="actions" trigger="hover" @command="onCommand">
        <i class="iconfont">&#xeb10;</i>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item :command="0">编辑</el-dropdown-item>
            <el-dropdown-item :command="1" divided v-if="value.defFlag==0">删除</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useStore } from '../store';
const props = defineProps<{ value: UserStar }>()
const { isSelf } = toRefs(useSpaceStore())
const { selectStar, saveUserStar, deleteUserStar } = useStore()
async function onCommand(cmd: number) {
  if (cmd == 0) {
    saveUserStar(props.value)
  } else if (cmd == 1) {
    await message.confirm(`您确定要删除收藏夹: ${props.value.starName} 吗?`)
    deleteUserStar(props.value)
  }
}
const folderIcon = computed(()=>{
  if(selectStar.value?.id === props.value.id) {
    return 'icon-24gl-folderOpen'
  } else if (props.value.starCount > 0) {
    return 'icon-24gl-folder2'
  } else {
    return 'icon-24gl-folder'
  }
})
const el = ref<HTMLElement>()
onMounted(()=>{
  if (selectStar.value?.id === props.value.id && selectStar.value.defFlag!==1) {
    el.value.scrollIntoView({ 
      behavior: 'smooth', 
      block: 'center'
    })
  }
})
</script>

<style scoped lang="scss">
.star-list-item {
  padding-right: 18px;
  height: 50px;
  line-height: 50px;
  display: flex;
  cursor: pointer;
  user-select: none;
  span:first-child {
    overflow: hidden;
    text-overflow: ellipsis;
  }
  &:hover:not(.dragging &) {
    background-color: #f4f5f7;
    html.dark & {
      background-color: #555;
    }
    .tip {
      .count {
        display: none;
      }
      .actions {
        display: inline-flex !important;
      }
      &:hover .star-popover {
        display: block;
      }
    }
    .drag-ico {
      visibility: visible;
      background-color: #d0d0d2;
    }
  }
  /* id选择器用于提高css优先级 */
  &#star-list-item[active=true] {
    background-color: #00aeec;
    color: #fff;
    .drag-ico {
      background-color: #57c0e6 !important;
    }
  }
}
.drag-ico {
  font-size: 8px;
  color: #eee;
  padding: 0 2px;
  margin-right: 8px;
  visibility: hidden;
  cursor: move;
}
.tip {
  margin-left: auto;
  width: 20px;
  height: 100%;
  text-align: center;
  font-size: 12px;
  font-weight: 550;
  position: relative;
}
.actions {
  height: 100%;
  align-items: center;
  color: inherit;
  display: none;
}
</style>