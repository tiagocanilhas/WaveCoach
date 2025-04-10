import { customFetch } from '../utils/customFetch'

import { URIS } from './uris'

export async function getAthletes() {
  return await customFetch(URIS.ATHLETES.getByCoach, 'GET')
}

export async function createAthlete(name: string, birthdate: string) {
  function toDisplayFormat(date: string): string {
    if (!date) return ''
    const [year, month, day] = date.split('-')
    return `${day}-${month}-${year}`
  }

  return await customFetch(URIS.ATHLETES.create, 'POST', { name, birthDate: toDisplayFormat(birthdate) })
}
