import { argv } from "process";
import fs from 'fs'
import path from "path";
import { execa } from 'execa';

let run = false
if (!isEmpty('./dist') && argv[2] != '-f') {
  const distStats = fs.statSync('./dist')
  const files = fs.readdirSync('./src', { recursive: true })
  for (const filepath of files) {
    const stats = fs.statSync('./src/' + filepath)
    if (stats.mtimeMs > distStats.mtimeMs) {
      run = true
      break
    }
  }
} else {
  run = true
}

function isEmpty(path) {
  return !fs.existsSync(path) || fs.readdirSync(path).length===0
}

if (run) {
  execa({stdout: ['pipe', 'inherit']}) `vite build`
} else {
  console.log('src更新时间<dist,跳过打包 -f强制打包');
}
