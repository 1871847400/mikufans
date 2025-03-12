import { request } from "../service";

export function getAppForm(tableName: string) {
  return request<AppForm>({
    url: '/admin/form/' + tableName,
    method: 'get',
  })
}