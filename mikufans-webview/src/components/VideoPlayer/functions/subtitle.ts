import { parse } from 'ass-compiler';
import subtitleApi from '@/apis/video/subtitle';
import { WebVTTParser } from 'webvtt-parser';
import srtParser2 from "srt-parser-2";

const webVTTParser = new WebVTTParser();
const srtParser = new srtParser2();

export async function addSubtitleTrack(subtitle: VideoSubtitle, video: HTMLVideoElement) {
  const text = await subtitleApi.getSubtitleText(subtitle.id)
  clearSubtitleTracks(video)
  const track = video.addTextTrack('subtitles', subtitle.region.langName, subtitle.region.langCode)
  switch (subtitle.type) {
    case 'ASS':
      const result = parse(text)
      for (const dia of result.events.dialogue) {
        track.addCue(new VTTCue(dia.Start, dia.End, dia.Text.raw))
      }
      break
    case 'SRT':
      const srtData = srtParser.fromSrt(text)
      for(const cue of srtData) {
        track.addCue(new VTTCue(cue.startSeconds, cue.endSeconds, cue.text))
      }
      break
    case 'VTT':
      const vttResult = webVTTParser.parse(text, 'subtitles')
      for(const cue of vttResult.cues) {
        track.addCue(new VTTCue(cue.startTime, cue.endTime, cue.text))
      }
      break
  }
  track.mode = 'showing'
}

/**
 * 清除字幕轨道,由于js添加的,无法拿到track元素删除,只能隐藏
 */
export function clearSubtitleTracks(video: HTMLVideoElement) {
  for (const track of Array.from(video.textTracks)) {
    track.mode = 'disabled'
  }
}