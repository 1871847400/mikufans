import Hls, { Events, FragmentLoaderContext, LoaderCallbacks, LoaderConfiguration } from "hls.js";
import videoResApi from "@/apis/video/resource";
import { getAccessToken, getAdminToken } from '@/utils/token';

export function createHls() {
  const hls = new Hls({
    maxBufferLength: 30,//小于30s加载新片段,无视maxBufferSize,
    maxBufferSize: 0,//缓存区不会超过这个大小(字节)
    enableWorker: true,
    // startLevel: 0, //设置默认画质
    xhrSetup(xhr, url) {
      const accessToken = url.includes('/admin/') ? getAdminToken() : getAccessToken()
      if (accessToken) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + accessToken)
      }
    },
    fLoader: class CustomLoader extends Hls.DefaultConfig.loader {
      async load(context: FragmentLoaderContext, config: LoaderConfiguration, callbacks: LoaderCallbacks<FragmentLoaderContext>) {
        // 重写获取分片成功后的逻辑
        const onSuccess = callbacks.onSuccess
        callbacks.onSuccess = async (res, stats, context, response: XMLHttpRequest)=>{
          // console.log(response, stats, context, response)
          res.data = await videoResApi.parseResponse(response)
          onSuccess(res, stats, context, response)
        }
        // 采用本来的load方法,之后会触发callbacks,如果完全重写load则需要主动回调
        super.load(context, config, callbacks)
      }
    },
  })
  hls.on(Events.ERROR, (e, data)=>{
    logger.error('hls error',data)
  })
  return hls
}