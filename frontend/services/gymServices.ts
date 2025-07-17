import { customFetch } from '../utils/customFetch'
import { toDisplayFormat } from '../utils/toDisplayFormat'
import { URIS } from './uris'

export async function createGymActivity(id: number, date: string, exercises: any[]) {
  return await customFetch(URIS.GYM.create, 'POST', { athleteId: id, date: toDisplayFormat(date), exercises })
}

export async function getGymActivity(gid: number) {
  return await customFetch(URIS.GYM.getById(gid), 'GET')
}

export async function updateGymActivity(gid: number, date: string, exercises: any[]) {
  return await customFetch(URIS.GYM.update(gid), 'PATCH', { date: date == null ? null : toDisplayFormat(date), exercises })
}

export async function deleteGymActivity(gid: number) {
  return await customFetch(URIS.GYM.delete(gid), 'DELETE')
}
