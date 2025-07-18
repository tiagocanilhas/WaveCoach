import * as React from 'react'
import { useEffect, useReducer, useState } from 'react'
import { useParams } from 'react-router-dom'

import { CircularProgress } from '@mui/material'
import { Workout } from '../../components/Workout'
import { AddGymWorkoutPopup } from '../../components/AddGymWorkoutPopup'

import { getCalendar } from '../../../../services/athleteServices'
import { getGymActivity } from '../../../../services/gymServices'

import { handleError } from '../../../../utils/handleError'
import { epochConverter } from '../../../../utils/epochConverter'

import { useAuthentication } from '../../hooks/useAuthentication'

import styles from './styles.module.css'

type State = {
  calendar: any[]
  isOpen: boolean
  workout: any
  selectedCycle?: { mesocycleId: number; microcycleId: number | null }
  error?: string
}

type Action =
  | { type: 'setActivities'; payload: any[] }
  | { type: 'setLastWorkout'; payload: any }
  | { type: 'addWorkout' }
  | { type: 'closeWorkout' }
  | { type: 'setSelectedCycle'; payload: { mesocycleId: number; microcycleId: number | null } | null }
  | { type: 'error'; error: string }

function reducer(state: State, action: Action) {
  switch (action.type) {
    case 'setActivities':
      return { ...state, calendar: action.payload }
    case 'setLastWorkout':
      return { ...state, workout: action.payload }
    case 'addWorkout':
      return { ...state, isOpen: true }
    case 'closeWorkout':
      return { ...state, isOpen: false }
    case 'setSelectedCycle':
      return { ...state, selectedCycle: action.payload }
    case 'error':
      return { ...state, error: action.error }
    default:
      return state
  }
}

export function GymWorkouts() {
  const initialState: State = { calendar: undefined, isOpen: false, workout: undefined }
  const [state, dispatch] = useReducer(reducer, initialState)
  const id = Number(useParams().aid)
  const [user] = useAuthentication()

  async function fetchCalendar() {
    try {
      const { res: calendar } = await getCalendar(id, 'gym')
      dispatch({ type: 'setActivities', payload: calendar })

      const activities = calendar.mesocycles
        .flatMap(mesocycle => mesocycle.microcycles)
        .flatMap(microcycle => microcycle.activities)

      if (activities.length === 0) {
        dispatch({ type: 'setLastWorkout', payload: null })
        return
      }

      const gid = activities[activities.length - 1].id
      const { res: workout } = await getGymActivity(gid)
      dispatch({ type: 'setLastWorkout', payload: workout })
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error.res) })
    }
  }

  useEffect(() => {
    fetchCalendar()
  }, [])

  function handleAdd() {
    dispatch({ type: 'addWorkout' })
  }

  function handleClose() {
    dispatch({ type: 'closeWorkout' })
  }

  function handleOnCycleSelected(cycle: { mesocycleId: number; microcycleId: number | null } | null) {
    dispatch({ type: 'setSelectedCycle', payload: cycle })
  }

  function handleOnSuccess() {
    fetchCalendar()
    dispatch({ type: 'closeWorkout' })
  }

  if (state.calendar === undefined || state.workout === undefined) return <CircularProgress />

  const lastWorkoutContent =
    state.workout === null
      ? undefined
      : {
          ...state.workout,
          id: null,
          date: new Date().toISOString().split('T')[0],
          exercises: state.workout.exercises.map(exercise => ({
            ...exercise,
            id: null,
            tempId: Date.now() + Math.random(),
            sets: exercise.sets.map(set => ({
              ...set,
              id: null,
              tempId: Date.now() + Math.random(),
            })),
          })),
        }

  return (
    <>
      <Workout
        lastWorkoutContent={
          <>
            {state.workout === undefined ? (
              <CircularProgress />
            ) : state.workout === null || state.workout.exercises.length === 0 ? (
              <p>Data not available</p>
            ) : (
              <div className={styles.lastWorkout}>
                <h2>{epochConverter(state.workout.date, 'dd-mm-yyyy')}</h2>
                {state.workout.exercises.map(exercise => (
                  <div className={styles.lastWorkoutExercise} key={exercise.id}>
                    <img src={exercise.url || `/images/no_image.svg`} alt="Exercise" />
                    <div className={styles.lastWorkoutExerciseContent}>
                      <h2>{exercise.name}</h2>
                      <ul>
                        {exercise.sets.map((set, idx) => (
                          <li key={set.id}>
                            Set {idx + 1}: {set.reps} x {set.weight} kg - {set.restTime}'
                          </li>
                        ))}
                      </ul>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </>
        }
        calendar={state.calendar}
        onAdd={user.isCoach ? handleAdd : undefined}
        onCycleSelected={handleOnCycleSelected}
        type="gym"
        onDeleteSuccess={fetchCalendar}
      />

      {state.isOpen && <AddGymWorkoutPopup workout={lastWorkoutContent} onClose={handleClose} onSuccess={handleOnSuccess} />}
    </>
  )
}
