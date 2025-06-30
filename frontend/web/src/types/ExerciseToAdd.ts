import { Exercise } from './Exercise'
import { SetDataToAdd } from './SetDataToAdd'

export type ExerciseToAdd = {
  tempId: string
  exercise: Exercise
  sets: SetDataToAdd[]
}
