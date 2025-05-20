import { customFetch } from '../utils/customFetch'

import { URIS } from './uris'

export async function createWaterManeuver(name: string) {
  return await customFetch(URIS.WATER_MANEUVERS.create, 'POST', { name })
}

export async function getWaterManeuvers() {
  return await customFetch(URIS.WATER_MANEUVERS.getAll, 'GET')
}
