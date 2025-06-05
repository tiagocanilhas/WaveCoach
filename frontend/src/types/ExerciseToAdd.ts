import { Exercise } from './Exercise'
import { SetData } from './SetData'

export type ExerciseToAdd = {
  tempId: string
  exercise: Exercise
  sets: SetData[]
}
