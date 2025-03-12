const canvs = document.createElement('canvas')
const ctx = canvs.getContext('2d')
export function measureText(font: string, fontSize: number, text: string) {
  ctx.font = `${fontSize}px ${font}`
  return ctx.measureText(text)
}