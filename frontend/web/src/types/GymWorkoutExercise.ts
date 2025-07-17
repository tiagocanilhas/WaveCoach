import { GymWorkoutSet } from './GymWorkoutSet'

export type GymWorkoutExercise = {
  id: number
  tempId?: number
  gymExerciseId?: number
  name?: string
  order?: number
  url?: string
  sets: GymWorkoutSet[]
}
