import { URIS } from './uris'
import { customFetch } from '../utils/customFetch'

export async function getWaterActivity(wid: string) {
  return customFetch(URIS.WATER.getById(wid), 'GET')
}
