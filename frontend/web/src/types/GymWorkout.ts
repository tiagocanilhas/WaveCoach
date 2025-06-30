import { GymWorkoutExercise } from './GymWorkoutExercise'

export type GymWorkout = {
  id: number
  date: number
  exercises: GymWorkoutExercise[]
}
