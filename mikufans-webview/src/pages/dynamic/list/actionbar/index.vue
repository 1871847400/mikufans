<template>
  <div class="flex items-center mt-2 gap-20">
    <div class="flex-1">
      <div @click="focus=focus==1?0:1" class="action-item truncate" :active="focus==1">
        <i class="iconfont icon-zhuanfa mr-1"></i>
        <span class="text-sm">{{ displayNumber(data.shares) || '转发' }}</span>
      </div>
    </div>
    <div class="flex-1">
      <div @click="focus=focus==2?0:2" class="action-item truncate" :active="focus==2">
        <i class="iconfont icon-pinglun mr-1"></i>
        <span class="text-sm">{{ displayNumber(data.commentArea.comments) || '评论' }}</span>
      </div>
    </div>
    <div class="flex-1">
      <div @click="like" class="action-item truncate" :active="likeVal===1">
        <i class="iconfont icon-dianzan mr-1"></i>
        <span class="text-sm">{{ displayNumber(likes) || '点赞' }}</span>
      </div>
    </div>
  </div>
  <dynamic-share v-if="focus==1" :data="data" />
  <comment-area v-else-if="focus==2" :area="data.commentArea" :use-hash="!!dynamicView" />
</template>

<script setup lang="ts">
import DynamicShare from './DynamicShare.vue';
import { displayNumber } from '@/utils/common';
import { useLike } from '@/hooks/useLike';
const props = defineProps<{
  data: UserDynamic
}>()
const { likes, likeVal } = toRefs(props.data.likeStatus)
const { like } = useLike(props.data.likeStatus)
const focus = ref(0)
//如果是动态详情页,默认打开评论并启用hash
const dynamicView = useRouteParams('id').value
if (dynamicView) {
  focus.value = 2
}
</script>

<style scoped lang="scss">
.action-item {
  width: 84px;
  cursor: pointer;
  user-select: none;
  &[active=true], &:hover {
    color: var(--blue0);
  }
}
</style>