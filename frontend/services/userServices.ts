import { customFetch } from '../utils/customFetch'

import { URIS } from './uris'

export async function login(username: string, password: string) {
  return await customFetch(URIS.USERS.login, 'POST', { username, password })
}

export async function logout(token?: string) {
  return await customFetch(URIS.USERS.logout, 'POST', undefined, token)
}

export async function checkAuth(token?: string) {
  return await customFetch(URIS.USERS.checkAuth, 'GET', undefined, token)
}

export async function updateUsername(username: string) {
  return await customFetch(URIS.USERS.updateUsername, 'PATCH', { newUsername: username })
}

export async function updatePassword(oldPassword: string, newPassword: string) {
  return await customFetch(URIS.USERS.updatePassword, 'PATCH', { oldPassword, newPassword })
}
