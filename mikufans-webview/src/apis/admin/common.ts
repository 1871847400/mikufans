import { AxiosRequestConfig } from "axios"
import { request } from "../service"

export function get<T>(url: string, params?: Record<string, any>, config?: AxiosRequestConfig) : Promise<T> {
  return request({
    url, 
    method: 'get',
    params, 
    ...config,
  })
}

export function post<T>(url: string, data: Record<string, any>, config?: AxiosRequestConfig) : Promise<T> {
  return request({
    url, 
    method: 'post',
    data, 
    ...config,
  })
}

export function put(url: string, data: Record<string, any>, config?: AxiosRequestConfig) : Promise<void> {
  return request({
    url, 
    method: 'put',
    data,  
    ...config,
  })
}

export function remove(url: string, config?: AxiosRequestConfig) : Promise<void> {
  return request({
    url, 
    method: 'delete',
    ...config,
  })
}