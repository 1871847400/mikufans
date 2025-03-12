<template>
  <div class="dynamic-item">
    <div>
      <user-avatar :user="data.user" size="55px"/>
    </div>
    <div class="flex-1">
      <div class="flex items-center justify-between">
        <div>
          <div class="text-lg text-bold">{{ data.user.nickname }}</div>
          <div class="text-[13px] grey2">{{ data.publishTimeStr }}</div>
        </div>
        <el-dropdown v-if="data.userId===userStore.id" @command="handlers[$event]()">
          <i class="iconfont cursor-pointer select-none" @click.prevent>&#xeb10;</i>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="remove">删除</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div v-if="data.shareId!='0'" class="my-2">
        <rich-text :content="data.shareReason" html :rows="3"/>
      </div>
      <div class="py-2 pr-[55px]" :class="{'bg-[#f5f5f5] px-4 mt-2 rounded': data.shareId!='0'}">
        <div v-if="data.shareId!='0'">
          <div class="flex items-center gap-1 py-1 text-sm">
            <user-avatar :user="data.source.user" size="24px" />
            <span>{{ data.source.user.nickname }}</span>
          </div>
        </div>
          <component :is="map[data.dynamicType]" :data="data"/>
          <!-- <template #fallback>
            <div class="flex-center gap-2 h-16">
              <img class="size-5 pb-1" :src="Loading" alt="">
              <span>加载中</span>
            </div>
          </template> -->
      </div>
      <action-bar :data="data" />
    </div>
  </div>
</template>

<script setup lang="ts">
import ActionBar from './actionbar/index.vue';
import userDynamicApi from '@/apis/user/dynamic';
const props = defineProps<{ data: UserDynamic }>()
const userStore = useUserStore()
const { emitEvent } = useDynamicStore()
const map = {} as Record<BusinessType, Component>
const result = import.meta.glob('../business/*.vue', { eager: true })
for (const [path, data] of Object.entries(result)) {
  //匹配组件名称 /a/b/cc.vue => cc
  const name = /([^\/\\]+)\.vue$/.exec(path)[1]
  map[name.toUpperCase() as BusinessType] = data['default']
}

const handlers = {
  async remove() {
    await message.confirm('您确定要删除这条动态吗？(无法恢复)')
    await userDynamicApi.removeById(props.data.id)
    emitEvent('delDynamic', props.data.id)
    message.success('删除成功')
  }
}
</script>

<style scoped lang="scss">
.dynamic-item {
  display: flex;
  gap: 16px;
  margin-bottom: 10px;
  background-color: var(--bg0);
  border-radius: 8px;
  padding: 16px;
  height: fit-content;
}
</style>