import { URIS } from './uris'
import { customFetch } from '../utils/customFetch'
import { toDisplayFormat } from '../utils/toDisplayFormat'

export async function createWaterActivity(
  athleteId: string,
  date: string,
  rpe: number,
  condition: string,
  trimp: number,
  duration: number,
  waves: { points?: number; rightSide: boolean; maneuvers?: { waterManeuverId: number; success: boolean }[] }[]
) {
  return customFetch(URIS.WATER.create, 'POST', {
    athleteId,
    date: toDisplayFormat(date),
    rpe,
    condition,
    trimp,
    duration,
    waves,
  })
}

export async function getWaterActivity(wid: string) {
  return customFetch(URIS.WATER.getById(wid), 'GET')
}

export async function updateWaterActivity(wid: string, date?: string, condition?: string, rpe?: number, duration?: number, trimp?: number, waves?: any[]){
  return customFetch(URIS.WATER.update(wid), 'PATCH', {
    date: date == null ? null : toDisplayFormat(date),
    condition,
    rpe,
    duration,
    trimp,
    waves
  })
}

export async function deleteWaterActivity(wid: string) {
  return customFetch(URIS.WATER.delete(wid), 'DELETE')
}

export async function createQuestionnaire(wid: string, sleep: number, fatigue: number, stress: number, musclePain: number) {
  return customFetch(URIS.WATER.createQuestionnaire(wid), 'POST', { sleep, fatigue, stress, musclePain })
}

export async function getQuestionnaire(wid: string) {
  return customFetch(URIS.WATER.getQuestionnaire(wid), 'GET')
}
