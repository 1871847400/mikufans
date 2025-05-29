import axios from "axios";
import { request } from "../service";

export type Params = {
  videoId: string
  partId: string
  watchPos: number
}

export type Data = {
  online: number //在线人数
}

export function getHeartbeat(params: Params) {
  return axios<Data>({
    // baseURL: 'http://localhost:3000',
    url: '/heartbeat',
    method: 'get',
    params
  })
}