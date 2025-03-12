<template>
  <el-input 
    v-model="keyword" 
    clearable 
    class="search-box" 
    placeholder="搜索视频、动态" 
    suffix-icon="Search"
    @keydown.enter="submit"
  ></el-input>
</template>

<script setup lang="ts">
const kw = useRouteQuery<string>('kw', '')
const keyword = ref(kw.value)
const router = useRouter()
const { userId } = toRefs(useSpaceStore())
function submit() {
  router.push({
    path: `/space/${userId.value}/dynamic`,
    query: {
      kw: keyword.value
    }
  })
}
</script>

<style scoped lang="scss">
.search-box {
  width: 140px;
  height: fit-content;
  margin-left: 40px;
  font-size: 12px;
  --el-border-radius-base: 14px;
}
</style>