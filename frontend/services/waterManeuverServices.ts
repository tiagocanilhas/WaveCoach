import { customFetch } from '../utils/customFetch'

import { URIS } from './uris'

export async function createWaterManeuver(name: string, image?: File) {
  const input = new Blob([JSON.stringify({ name })], { type: 'application/json' })
  const data = new FormData()
  data.append('input', input)
  if (image) data.append('photo', image)

  return await customFetch(URIS.WATER_MANEUVERS.create, 'POST', data)
}

export async function getWaterManeuvers() {
  return await customFetch(URIS.WATER_MANEUVERS.getAll, 'GET')
}
