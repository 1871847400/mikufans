import { execa } from 'execa';
import fs from 'fs';
// const { stdout } = execa({stdout: ['pipe', 'inherit']}) `yarn dev`
// // console.log('stdout', stdout);
// stdout.on('close', (chunk)=>{
//   console.log('close', chunk);
// })

console.log(fs.readdirSync('./dist').length==0);
