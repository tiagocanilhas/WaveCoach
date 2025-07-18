import * as React from 'react'
import { useParams } from 'react-router-dom'

import { GymWorkoutPopup } from '../../components/GymWorkoutPopup'

import { updateGymActivity } from '../../../../services/gymServices'

import { WorkoutEditing } from '../../../../utils/WorkoutEditing'
import { diffListOrNull } from '../../../../utils/diffListOrNull'
import { epochConverter } from '../../../../utils/epochConverter'

import { GymWorkout } from '../../types/GymWorkout'
import { GymWorkoutExercise } from '../../types/GymWorkoutExercise'
import { GymWorkoutSet } from '../../types/GymWorkoutSet'

type EditGymWorkoutPopup = {
  workout: GymWorkout
  onClose: () => void
  onSuccess: () => void
}

export function EditGymWorkoutPopup({ workout, onClose, onSuccess }: EditGymWorkoutPopup) {
  const gid = Number(useParams().gid)

  async function handleOnSave(stateDate: string, stateExercises: any[], stateRemovedExercises: any[]) {
    const date = stateDate === epochConverter(workout.date, 'yyyy-mm-dd') ? null : stateDate

    const exercises: GymWorkoutExercise[] = diffListOrNull(stateExercises, (exercise, index) => {
        const original = workout.exercises.find(e => e.id === exercise.id)

        const newExercise = {
          id: exercise.id,
          gymExerciseId: exercise.gymExerciseId,
          order: WorkoutEditing.checkOrder(index, exercise.order),
          sets: diffListOrNull(exercise.sets, (set: GymWorkoutSet, setIndex) => {
            if (WorkoutEditing.checkDeleteObject(set)) return set

            const originalSet = original?.sets.find(s => s.id === set.id) || {}

            const newSet = {
              id: set.id,
              reps: WorkoutEditing.onlyIfDifferent('reps', set, originalSet),
              weight: WorkoutEditing.onlyIfDifferent('weight', set, originalSet),
              restTime: WorkoutEditing.onlyIfDifferent('restTime', set, originalSet),
              order: WorkoutEditing.checkOrder(setIndex, set.order),
            }

            return WorkoutEditing.noEditingMade(newSet) ? null : newSet
          }),
        }

        return WorkoutEditing.noEditingMade(newExercise) ? null : newExercise
      }) ?? []
      
    await updateGymActivity(gid, date, [...exercises, ...stateRemovedExercises])
  }

  return <GymWorkoutPopup workout={workout} isNew={false}  onClose={onClose} onSave={handleOnSave} onSuccess={onSuccess} />
}
