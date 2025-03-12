import { request } from "@/apis/service";

export default {
  getDictData
}

function getDictTypes(params: SearchParams) {
  return request<Page<SysDictType>>({
    url: '/admin/system/dict',
    method: 'get',
    params,
  })
}

function getDictData(dictType: string) {
  return request<SysDictData[]>({
    url: '/system/dict/data/' + dictType,
    method: 'get',
    __cache__: true,
  })
}