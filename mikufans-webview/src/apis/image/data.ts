import axios, { ResponseType } from "axios";
import { imageUrl } from ".";

export function getImageData(resId: string, responseType: ResponseType) {
  return axios({
    url: imageUrl + resId,
    method: 'get',
    responseType
  })
}