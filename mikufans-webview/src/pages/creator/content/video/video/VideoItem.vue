<template>
  <div class="flex items-center gap-4 mb-4">
    <a class="flex-shrink-0 self-start" :href="'/video/'+video.sid" target="_blank" draggable="false">
      <miku-image v-if="bangumi" :res-id="bangumi.posterId" poster class="w-[120px]" draggable="false" />
      <miku-image v-else :res-id="video.bannerId" video class="w-[200px]" draggable="false"/>
    </a>
    <div class="flex-1 self-stretch flex flex-col">
      <a class="w-fit text-base pt-1 pb-2" :href="'/video/'+video.sid" target="_blank">
        <text-button class="maxline-1 text-base select-auto">{{ video.title }}</text-button>
      </a>
      <rich-text class="grey1 text-xs" html :content="video.intro" :rows="2" />
      <div class="flex gap-4 text-sm mt-auto pt-2">
        <div v-if="video.disabled">
          <el-text type="danger" class="mr-2">视频已被封禁,原因：{{ video.reason }}</el-text>
          <el-link type="primary" @click="apply">申请恢复</el-link>
        </div>
        <template v-else>
          <span v-if="video.statusCountMap.UNKNOWN" class="blue0">
            <i class="iconfont icon-shouye2 mr-1"></i>审核中:{{ video.statusCountMap.UNKNOWN }}
          </span>
          <span v-if="video.statusCountMap.FAIL" class="text-red-400">
            <i class="iconfont icon-jinggao mr-1"></i>未通过:{{ video.statusCountMap.FAIL }}
          </span>
        </template>
      </div>
      <video-stats :video="video" />
    </div>
    <div class="flex">
      <el-button @click="actions[0].click()" class="iconfont icon-xiugai1 gap-1 text-sm">
        <span class="xl:hidden">编辑</span>
      </el-button>
      <!-- <el-button @click="actions[1].click()" class="iconfont icon-gengduo5 gap-1 text-sm">
        <span class="xl:hidden">分集</span>
      </el-button> -->
      <el-popover
        placement="bottom-end"
        :width="400"
        trigger="hover"
      >
        <template #reference>
          <el-button class="iconfont icon-24gf-ellipsisVertical w-6"></el-button>
        </template>
        <div>
          <div class="flex gap-4 flex-wrap p-4">
            <template v-for="{ label, icon, click } in actions">
              <text-button class="flex items-center flex-col gap-1" @click="click">
                <i class="iconfont text-[24px]" :class="icon"></i>
                <span class="text-xs">{{ label }}</span>
              </text-button>
            </template>
          </div>
          <el-divider class="m-0" />
          <text-button class="w-full py-4 gap-1 grey1" @click="remove">
            <i class="iconfont icon-shanchu"></i>
            <span>删除稿件</span>
          </text-button>
        </div>
      </el-popover>
    </div>
  </div>
</template>

<script setup lang="ts">
import videoApi from '@/apis/video';
import { openDialog } from '@/utils/dialog';
import VisibleDialog from '../dialog/VisibleDialog.vue';
const { video } = defineProps<{ video: Video }>()
const emits = defineEmits<{ update: [] }>()
const bangumi = computed(()=>video.type!='VIDEO'?video.bangumi:null)
const router = useRouter()
const actions = [
  {
    label: '编辑稿件',
    icon: 'icon-bianji',
    click() {
      router.push('/creator/upload/video?vid='+video.id)
    }
  },
  // {
  //   label: '分集管理',
  //   icon: 'icon-bianji',
  //   click() {
  //     router.push({
  //       path: route.path,
  //       query: {
  //         type: route.query.type,
  //         vid: video.id
  //       }
  //     })
  //   }
  // },
  {
    label: '可见范围',
    icon: 'icon-xianshimima',
    click() {
      openDialog(VisibleDialog, {
        video,
      })
    },
  }
]
async function apply() {
  await message.confirm('你确定要申请吗?')
  await videoApi.apply(video.id)
  message.success('已申请,请耐心等待通知！')
  emits('update')
}
async function remove() {
  await message.confirm(`您确定要删除这个视频 ${video.title}`)
  await videoApi.remove(video.id)
  message.success('删除视频成功！')
  emits('update')
}
</script>

<style scoped lang="scss">
/* .video-title {
  height: 48px; //两倍行高
  margin: 4px 0;
  a {
    width: fit-content;
    font-size: 16px;
    line-height: 24px;
    cursor: pointer;
    &:hover {
      color: var(--blue0);
    }
  }
} */
</style>