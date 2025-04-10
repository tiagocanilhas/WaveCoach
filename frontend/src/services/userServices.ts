import { customFetch } from '../utils/customFetch'

import { URIS } from './uris'

export async function login(username: string, password: string) {
  return await customFetch(URIS.USERS.login, 'POST', {
    username: username,
    password: password,
  })
}

export async function register(username: string, password: string) {
  return await customFetch(URIS.USERS.create, 'POST', {
    username: username,
    password: password,
  })
}
