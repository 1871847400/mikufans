/**
 * 动态获取元素的变换值
 * a = 2，表示元素在水平方向上放大 2 倍。
 * b = 0，表示没有垂直方向对水平方向的斜切效果。
 * c = 0，表示没有水平方向对垂直方向的斜切效果。
 * d = 2，表示元素在垂直方向上放大 2 倍。
 * tx = 100，表示元素向右移动 100 像素。
 * ty = 50，表示元素向下移动 50 像素。
 */
export function parseTransform(el: HTMLElement) {
  const transform = getComputedStyle(el).transform
  const values = transform.match(/matrix\((.+)\)/)?.[1]?.split(',')?.map(Number) || []
  return {
    a: values[0] ?? 0,
    b: values[1] ?? 0,
    c: values[2] ?? 0,
    d: values[3] ?? 0,
    tx: values[4] ?? 0,
    ty: values[5] ?? 0,
  }
}