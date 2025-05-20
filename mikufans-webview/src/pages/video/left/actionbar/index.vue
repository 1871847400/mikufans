<template>
  <div class="action-bar">
    <like-button/>
    <!-- <dislike-button/> -->
    <coin-button/>
    <star-button/>
    <share-button/>
    <more-actions class="ml-auto" />
  </div>
</template>

<script setup lang="ts">
import LikeButton from './buttons/LikeButton.vue';
import DislikeButton from './buttons/DislikeButton.vue';
import CoinButton from './buttons/CoinButton.vue';
import StarButton from './buttons/StarButton.vue';
import ShareButton from './buttons/ShareButton.vue';
import MoreActions from './mores/index.vue'
</script>

<style scoped lang="scss">
.action-bar {
  display: flex;
  margin-top: 4px;
  height: 55px;
  align-items: center;
  border-bottom: 1px solid #e3e5e7;
}
.action-item {
  width: 100px;
  padding-left: 4px;
  cursor: pointer;
  user-select: none;
  color: var(--grey1);
  display: flex;
  align-items: center;
  white-space: nowrap;
  position: relative;
  transition: color .2s;
  &:hover, &.active {
    color: var(--pink0);
  }
  :deep(.number) {
    font-size: 16px;
    position: absolute;
    left: 48px;
    padding-top: 2px; //让数字稍微下沉
  }
}
</style>