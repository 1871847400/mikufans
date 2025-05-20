import { convertToHtml } from "@/utils/emoji"

export const HASH_PREFIX = '#comment'

/**
 * 转换评论内容为HTML格式
 */
export function convertComment(comment: UserComment) {
  let result = convertToHtml(comment.content)
  for (const nickname in comment.atUsers) {
    const userId = comment.atUsers[nickname]
    result = result.replaceAll('@'+nickname, 
      `<a class="at-user" href="/space/${userId}" target="_blank">@${nickname}</a>`
    )
  }
  return result
}