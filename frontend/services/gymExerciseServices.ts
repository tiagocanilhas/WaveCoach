import { customFetch } from '../utils/customFetch'

import { URIS } from './uris'

export async function createGymExercise(name: string, category: string, image?: File) {
  const input = new Blob([JSON.stringify({ name, category })], { type: 'application/json' })
  const data = new FormData()
  data.append('input', input)
  if (image) data.append('photo', image)

  return await customFetch(URIS.GYM_EXERCISES.create, 'POST', data)
}

export async function getGymExercises() {
  return await customFetch(URIS.GYM_EXERCISES.getAll, 'GET')
}

export async function getGymExerciseById(id: number) {
  return await customFetch(URIS.GYM_EXERCISES.getById(id), 'GET')
}

export async function updateGymExercise(id: number, name: string, category: string) {
  return await customFetch(URIS.GYM_EXERCISES.update(id), 'PUT', {
    name: name,
    category: category,
  })
}

export async function deleteGymExercise(id: number) {
  return await customFetch(URIS.GYM_EXERCISES.delete(id), 'DELETE')
}
