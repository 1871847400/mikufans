<template>
  <div class="bangumi-page">
    <nav-bar transparent virtual />
    <Async :loading="loading" :error="error" :empty="!video" empty-text="没有找到视频" height="100vh">
      <top-section/>
      <div class="w-full bg0">
        <div class="w-[80%] mx-auto">
          <thumb-tab v-model="type" :options="options" class="h-[60px]" style="gap: 30px"/>
        </div>
      </div>
      <div class="py-5 bg1">
        <div class="w-[80%] mx-auto min-h-[250px]">
          <router-view v-slot="{ Component }">
            <transition mode="out-in">
              <keep-alive>
                <component :is="Component"/>
              </keep-alive>
            </transition>
          </router-view>
        </div>
      </div>
    </Async>
  </div>
</template>

<script setup lang="ts">
import TopSection from './top/index.vue';
import { useBangumiStore } from '@/stores/bangumi';
import videoApi from '@/apis/video';
const { video } = toRefs(useBangumiStore())
const route = useRoute()
const router = useRouter()
const id = useRouteParams<string>('id')
const type = computed({
  get: ()=>route.path,
  set(val) {
    router.push(val)
  }
})
const options = computed(()=>[
  {
    label: '作品详情',
    value: `/bangumi/${id.value}`
  },
  {
    label: '用户点评',
    value: `/bangumi/${id.value}/comment`
  },
  {
    label: '相关视频',
    value: `/bangumi/${id.value}/refer`,
  },
])
const loading = ref(true)
const error = ref(null)
watchImmediate(id, async (id)=>{
  try {
    loading.value = true
    video.value = await videoApi.getById(id+'')
    if (!video.value || !video.value.bangumi) {
      throw new Error
    }
    document.title = video.value.title + ' - Mikufans'
  } catch(err) {
    error.value = err
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.bangumi-page {
  min-height: 100vh;
  :deep(.navbar-inner[transparent=true]) {
    background: transparent;
  }
}
.v-enter-active,
.v-leave-active {
  transition: all .25s ease;
}
.v-enter-from,
.v-leave-to {
  opacity: 0;
}
</style>