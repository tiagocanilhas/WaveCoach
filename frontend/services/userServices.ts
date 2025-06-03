import { customFetch } from '../utils/customFetch'

import { URIS } from './uris'

export async function login(username: string, password: string) {
  return await customFetch(URIS.USERS.login, 'POST', { username, password })
}

export async function logout() {
  return await customFetch(URIS.USERS.logout, 'POST')
}

export async function checkAuth() {
  return await customFetch(URIS.USERS.checkAuth, 'GET')
}

export async function update(username: string, password: string) {
  return await customFetch(URIS.USERS.update, 'PUT', { username, password })
}
