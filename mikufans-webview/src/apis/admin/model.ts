import { get, post, put, remove } from "./common";

export async function getModel<T>(model: string, params?: Partial<T>) {
  const models = await get<T[]>('/admin/model/'+model, params)
  return Array.isArray(models) ? models[0] : null
}

export function listModel<T>(model: string, params?: Partial<T>) {
  return get<T[]>('/admin/model/'+model, params)
}

export function pageModel<T>(model: string, params?: Partial<T> | SearchParams) {
  return get<Page<T>>(`/admin/model/${model}/page`, params)
}

export function createModel<T>(model: string, data: Partial<T>) {
  return post<T>('/admin/model/'+model, data)
}

export function updateModel<T extends object>(model: string, data: Partial<T>) {
  return put('/admin/model/'+model, data)
}

export function removeModel(model: string, id: string | string[], ...ids: string[]) {
  return remove(`/admin/model/${model}/${[...(Array.isArray(id) ? id : [id]), ...ids].join(',')}`)
}

export default {
  getModel,
  listModel,
  pageModel,
  createModel,
  updateModel,
  removeModel,
}