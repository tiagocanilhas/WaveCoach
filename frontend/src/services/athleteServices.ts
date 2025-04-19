import { customFetch } from '../utils/customFetch'

import { URIS } from './uris'

function toDisplayFormat(date: string): string {
  if (!date) return ''
  const [year, month, day] = date.split('-')
  return `${day}-${month}-${year}`
}

export async function getAthlete(id: string) {
  return await customFetch(URIS.ATHLETES.getById(id), 'GET')
}

export async function getAthletes() {
  return await customFetch(URIS.ATHLETES.getByCoach, 'GET')
}

export async function createAthlete(name: string, birthdate: string) {
  return await customFetch(URIS.ATHLETES.create, 'POST', { name, birthDate: toDisplayFormat(birthdate) })
}

export async function updateAthlete(id: string, name: string, birthdate: string) {
  return await customFetch(URIS.ATHLETES.update(id), 'PUT', { name, birthDate: toDisplayFormat(birthdate) })
}

export async function deleteAthlete(id: string) {
  return await customFetch(URIS.ATHLETES.delete(id), 'DELETE')
}

export async function generateCode(id: string) {
  return await customFetch(URIS.ATHLETES.generateCode(id), 'POST')
}

export async function getAthleteByCode(code: string) {
  return await customFetch(URIS.ATHLETES.getByCode(code), 'GET')
}

export async function changeAthleteCredentials(code: string, username: string, password: string) {
  return await customFetch(URIS.ATHLETES.changeCredentials, 'POST', { code, username, password })
}
