import axios from 'axios';
import { WebVTTParser } from 'webvtt-parser';
import { MaybeRef } from 'vue';
const parser = new WebVTTParser();
export function useThumbnail(url: MaybeRef<string>) {
  const metadata = ref<{ 
    start: number, 
    end: number, 
    x: number
    y: number
    w: number
    h: number
  }[]>([])
  if (isRef(url)) {
    watchImmediate(url, request)
  } else {
    request()
  }
  async function request() {
    const _url = unref(url)
    if (!_url) {
      return
    }
    const { data } = await axios({
      url: _url,
      responseType: 'text'
    })
    const tree = parser.parse(data, 'metadata');
    //thumbnails.jpg#xywh=384,324,192,108
    for (const cue of tree.cues) {
      const arr = cue.text.split('=')[1].split(',').map(Number)
      metadata.value.push({
        start: cue.startTime,
        end: cue.endTime,
        x: arr[0],
        y: arr[1],
        w: arr[2],
        h: arr[3],
      })
    }
  }
  return metadata
}