import { customFetch } from '../utils/customFetch'
import { toDisplayFormat } from '../utils/toDisplayFormat'
import { URIS } from './uris'

export async function createGymActivity(id: string, date: string, exercises: any[]) {
  return await customFetch(URIS.GYM.create, 'POST', { athleteId: id, date: toDisplayFormat(date), exercises})
}

export async function getGymActivity(id: string) {
  return await customFetch(URIS.GYM.getById(id), 'GET')
}
