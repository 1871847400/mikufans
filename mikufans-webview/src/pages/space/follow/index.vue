<template>
  <div class="flex">
    <Sidebar/>
    <div class="flex-1 p-4">
      <div class="flex items-center">
        <span class="text-lg mr-auto">{{ fans ? '全部粉丝' : '全部关注' }}</span>
        <template v-if="!fans">
          <el-radio-group v-model="sort" size="small">
            <el-radio-button label="最常访问" value="ACCESS" />
            <el-radio-button label="最近关注" value="FOLLOW_TIME" />
          </el-radio-group>
          <search-box class="ml-5" v-model="keyword" :debounce="800"/>
        </template>
      </div>
      <el-divider class="mt-5 mb-2" />
      <async :loading="loading" :error="error" :empty="page?.empty" min-h="320px">
        <ul>
          <template v-for="i in page.records" :key="i.id">
            <li class="flex items-center gap-4 py-4">
              <user-avatar :user="i" size="54px"/>
              <div>
                <a class="block w-fit" :href="'/space/'+i.id" target="_blank">
                  <text-button class="text-base mb-2" v-html="i.highlighted||i.nickname"/>
                </a>
                <div class="maxline-1 text-xs grey2">{{ i.sign || '个性签名' }}</div>
              </div>
              <div class="ml-auto mr-4">
                <user-follow-button :user-id="i.id" :status="i.follow" />
              </div>
            </li>
          </template>
        </ul>
        <miku-pagination v-model="pageNum" :page="page" />
      </async>
    </div>
  </div>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import Sidebar from './Sidebar.vue';
import userFollowApi from '@/apis/user/follow';
const { userId, isSelf } = toRefs(useSpaceStore())
const sort = useRouteQuery<UserFollowSearch['sort']>('sort', 'ACCESS')
const pageNum = useRouteQuery('page', 1)
const keyword = ref('')
const { page, loading, error, run } = usePage(search)
const route = useRoute()
const fans = computed(()=>route.path.includes('/fans'))
function search(pageNum: number) {
  if (fans.value) {
    return userFollowApi.getFans({
      userId: userId.value,
      pageNum,
      pageSize: 10,
    })
  } else {
    return userFollowApi.getFollows({
      userId: userId.value,
      keyword: keyword.value,
      sort: sort.value,
      pageNum,
      pageSize: 10,
    })
  }
}
watch(pageNum, run) 
watchImmediate([keyword, fans, sort], ()=>{
  run(1)
  pageNum.value = 1
})
</script>

<style scoped lang="scss">
</style>