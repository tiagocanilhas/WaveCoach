import { customFetch } from '../utils/customFetch'
import { URIS } from './uris'

export async function createGymActivity(id: string) {
  return await customFetch(URIS.GYM.create, 'POST', { name: id })
}

export async function getGymActivity(id: string) {
  return await customFetch(URIS.GYM.getById(id), 'GET')
}
