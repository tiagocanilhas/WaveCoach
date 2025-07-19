import * as React from 'react'
import { useParams } from 'react-router-dom'

import { GymWorkoutPopup } from '../../components/GymWorkoutPopup'

import { createGymActivity } from '../../../../services/gymServices'

import { GymWorkout } from '../../types/GymWorkout'

type AddGymWorkoutPopup = {
  workout: GymWorkout
  onClose: () => void
  onSuccess: () => void
}

export function AddGymWorkoutPopup({ workout, onClose, onSuccess }: AddGymWorkoutPopup) {
  const id = Number(useParams().aid)

  async function handleOnSave(stateDate: string, stateExercises: any[]) {
    const date = stateDate
    const exercises = stateExercises.map(info => ({
      gymExerciseId: info.gymExerciseId,
      sets: info.sets.map(set => ({
        reps: set.reps,
        weight: set.weight,
        restTime: set.restTime,
      })),
    }))

    await createGymActivity(id, date, exercises)
  }

  return <GymWorkoutPopup workout={workout} isNew={true} onClose={onClose} onSave={handleOnSave} onSuccess={onSuccess} />
}
