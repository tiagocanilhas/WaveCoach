import { customFetch } from '../utils/customFetch'

import { URIS } from './uris'

export async function createGymExercise(name: string, category: string) {
  return await customFetch(URIS.GYM_EXERCISES.create, 'POST', {
    name: name,
    category: category,
  })
}

export async function getGymExercises() {
  return await customFetch(URIS.GYM_EXERCISES.getAll, 'GET')
}

export async function getGymExerciseById(id: string) {
  return await customFetch(URIS.GYM_EXERCISES.getById(id), 'GET')
}

export async function updateGymExercise(id: string, name: string, category: string) {
  return await customFetch(URIS.GYM_EXERCISES.update(id), 'PUT', {
    name: name,
    category: category,
  })
}

export async function deleteGymExercise(id: string) {
  return await customFetch(URIS.GYM_EXERCISES.delete(id), 'DELETE')
}
