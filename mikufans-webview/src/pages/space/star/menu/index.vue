<template>
  <div class="user-star-menu">
    <el-button 
      v-if="isSelf" 
      type="primary" 
      size="large" 
      class="w-full mb-2" 
      @click="saveUserStar()"
    >
      <i class="iconfont icon-24gl-folderPlus mr-2"></i>
      <span class="mr-1" style="letter-spacing: 3px;">新建收藏夹</span>
      <span class="text-xs text-[#e5e5e5]">[{{ userStars.length }}/100]</span>
    </el-button>
    <!-- 最大显示高度700px,超过后显示滚动条 -->
    <el-scrollbar max-height="700px">
      <sortable
        v-model="userStars" 
        item-key="id" 
        v-slot="{ item }" 
        :sortable="i=>i.defFlag==0 && isSelf"
        @change="saveOrder"
      >
        <list-item :value="item" />
      </sortable>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { useStore } from '../store';
import ListItem from './ListItem.vue';
import userStarApi from '@/apis/user/star';
const { isSelf, userId } = toRefs(useSpaceStore())
const { userStars, saveUserStar } = useStore()
watchImmediate(userId, async (userId)=>{
  userStars.value = await userStarApi.search({ userId })
})
//改变收藏夹排序
function saveOrder(startIndex: number, curIndex: number) {
  if (startIndex !== curIndex) {
    userStarApi.changeOrder(userStars.value.map(i=>i.id))
  }
}
</script>

<style scoped lang="scss">
.user-star-menu {
  width: 200px;
  padding-bottom: 20px;
  flex-shrink: 0;
  border-right: 1px solid #eee;
}
</style>