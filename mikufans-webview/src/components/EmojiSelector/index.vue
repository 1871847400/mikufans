<template>
  <div class="emoji-selector" @mousedown.stop>
    <div class="emoji-title">{{ emojiMap[index].pname }}</div>
    <el-scrollbar height="150px" class="scrollbar">
      <ul class="emoji-list">
        <template v-for="i in emojiMap[index].emojis">
          <li class="emoji-item" @click="insert(i)">
            <span v-if="i.type===4">{{ i.name }}</span>
            <img v-else :src="i.url" :alt="i.name" :title="i.name" :data-size="emojiMap[index].size" draggable="false">
          </li>
        </template>
      </ul>
    </el-scrollbar>
    <ul class="emoji-type-list">
      <template v-for="i,k in emojiMap">
        <li class="emoji-type-item" @click="index=k" :data-active="index===k">
          <img :src="i.purl" :alt="i.pname" draggable="false">
        </li>
      </template>
    </ul>
  </div>
</template>

<script setup lang="ts">
import RichInput from '@/components/RichInput/index.vue';
import { EmojiItem, emojiMap } from '@/utils/emoji'
const props = defineProps<{
  inputRef?: InstanceType<typeof RichInput>
}>()
const index = ref(0)
function insert(emoji: EmojiItem) {
  if (emoji.type === 4) {
    props.inputRef.insertNode(new Text(emoji.name))
  } else {
    props.inputRef.insertEmoji(emoji)
  }
}
</script>

<style scoped lang="scss">
.emoji-selector {
  background-color: var(--bg0);
  border-radius: 8px;
  overflow: hidden;
}
.emoji-title {
  font-size: 14px;
  text-indent: 8px;
  line-height: 32px;
  color: var(--grey0);
}
.scrollbar {
  height: fit-content;
  padding: 0 8px;
}
.emoji-list {
  display: flex;
  flex-wrap: wrap;
  .emoji-item {
    padding: 6px;
    cursor: pointer;
    user-select: none;
    &:hover {
      background-color: #f1f2f3;
    }
    img {
      width: 24px;
      height: 24px;
      &[data-size='2'] {
        width: 40px;
        height: 40px;
      }
    }
    span {
      font-size: 12px;
      color: #666;
    }
  }
}
.emoji-type-list {
  display: flex;
  align-items: center;
  background-color: #f1f2f3;
  .emoji-type-item {
    width: 60px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    user-select: none;
    cursor: pointer;
    &:hover, &[data-active=true] {
      background-color: #fff;
    }
    img {
      width: 24px;
      height: 24px;
    }
  }
}
</style>