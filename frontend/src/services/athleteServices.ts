import { customFetch } from '../utils/customFetch'
import { URIS } from './uris'
import { toDisplayFormat } from '../utils/toDisplayFormat'

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

export async function getCharacteristics(id: string) {
  return await customFetch(URIS.ATHLETES.getCharacteristicsList(id), 'GET')
}

export async function createCharacteristics(
  id: string,
  date: string,
  height: number,
  weight: number,
  calories: number,
  bodyFat: number,
  waistSize: number,
  armSize: number,
  thighSize: number,
  tricepFat: number,
  abdomenFat: number,
  thighFat: number
) {
  return await customFetch(URIS.ATHLETES.createCharacteristics(id), 'POST', {
    date: toDisplayFormat(date),
    height,
    weight,
    calories,
    bodyFat,
    waistSize,
    armSize,
    thighSize,
    tricepFat,
    abdomenFat,
    thighFat,
  })
}

export async function updateCharacteristics(
  id: string,
  date: string,
  height: number,
  weight: number,
  calories: number,
  bodyFat: number,
  waistSize: number,
  armSize: number,
  thighSize: number,
  tricepFat: number,
  abdomenFat: number,
  thighFat: number
) {
  return await customFetch(URIS.ATHLETES.updateCharacteristics(id, date), 'PUT', {
    date: toDisplayFormat(date),
    height,
    weight,
    calories,
    bodyFat,
    waistSize,
    armSize,
    thighSize,
    tricepFat,
    abdomenFat,
    thighFat,
  })
}

export async function deleteCharacteristics(id: string, date: string) {
  return await customFetch(URIS.ATHLETES.deleteCharacteristics(id, date), 'DELETE')
}

export async function createCalendar(id: string, events: { mesocycles: any[] }) {
  return await customFetch(URIS.ATHLETES.createCalendar(id), 'POST', events)
}

export async function getCalendar(id: string, type?: string) {
  return await customFetch(URIS.ATHLETES.getCalendar(id, type), 'GET')
}
