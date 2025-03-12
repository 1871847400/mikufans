<template>
  <i v-if="dict" class="user-gender iconfont" :class="{ [dict.iconName]: true, background }" :title="'性别：'+dict.dictLabel"></i>
</template>

<script setup lang="ts">
import { useDictData } from '@/hooks/useDictData';
const props = defineProps({
  gender: any<User['gender']>().isRequired,
  background: bool().def(true)
})
const dict = useDictData('user_gender', toRef(props, 'gender'))
</script>

<style scoped lang="scss">
.user-gender {
  width: 18px;
  height: 18px;
  font-size: 10px;
  line-height: 1.8;
  text-align: center;
  font-weight: 600;
  border-radius: 50%;
  color: v-bind('dict?.iconColor');
  &.background {
    color: #fff;
    background-color: v-bind('dict?.iconColor');
  }
}
</style>