import { URIS } from './uris'
import { customFetch } from '../utils/customFetch'
import { toDisplayFormat } from '../utils/toDisplayFormat'

export async function createWaterActivity(
  athleteId: string,
  date: string,
  pse: number,
  condition: string,
  heartRate: number,
  duration: number,
  waves: { points?: number; rightSide: boolean; maneuvers?: { waterManeuverId: number; success: boolean }[] }[]
) {
  return customFetch(URIS.WATER.create, 'POST', {
    athleteId,
    date: toDisplayFormat(date),
    pse,
    condition,
    heartRate,
    duration,
    waves,
  })
}

export async function getWaterActivity(wid: string) {
  return customFetch(URIS.WATER.getById(wid), 'GET')
}

export async function createQuestionnaire(wid: string, sleep: number, fatigue: number, stress: number, musclePain: number) {
  return customFetch(URIS.WATER.createQuestionnaire(wid), 'POST', { sleep, fatigue, stress, musclePain })
}

export async function getQuestionnaire(wid: string) {
  return customFetch(URIS.WATER.getQuestionnaire(wid), 'GET')
}
