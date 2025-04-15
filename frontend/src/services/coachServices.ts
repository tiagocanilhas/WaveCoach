import { customFetch } from '../utils/customFetch'

import { URIS } from './uris'

export async function createCoach(username: string, password: string) {
  return await customFetch(URIS.COACHES.create, 'POST', {
    username: username,
    password: password,
  })
}
