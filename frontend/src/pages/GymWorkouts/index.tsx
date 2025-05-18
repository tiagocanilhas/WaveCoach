import * as React from 'react'
import { useEffect, useReducer, useState } from 'react'
import { useParams } from 'react-router-dom'

import { CircularProgress } from '@mui/material'
import { Workout } from '../../components/Workout'
import { AddWorkout } from '../../components/AddWorkout'

import { getCalendar } from '../../services/athleteServices'
import { getGymActivity } from '../../services/gymServices'

import styles from './styles.module.css'
import { useAuthentication } from '../../hooks/useAuthentication'

type State = {
  activities: any[]
  isOpen: boolean
  workout: any
}

type Action =
  | { type: 'setActivities'; payload: any[] }
  | { type: 'setLastWorkout'; payload: any }
  | { type: 'addWorkout' }
  | { type: 'closeWorkout' }

function reducer(state: State, action: Action) {
  switch (action.type) {
    case 'setActivities':
      return { ...state, activities: action.payload }
    case 'setLastWorkout':
      return { ...state, workout: action.payload }
    case 'addWorkout':
      return { ...state, isOpen: true }
    case 'closeWorkout':
      return { ...state, isOpen: false }
    default:
      return state
  }
}

export function GymWorkouts() {
  const initialState: State = { activities: undefined, isOpen: false, workout: undefined }
  const [state, dispatch] = useReducer(reducer, initialState)
  const id = useParams().aid
  const [user] = useAuthentication()

  useEffect(() => {
    async function fetchLastWorkout(gid: string) {
      try {
        const res = await getGymActivity(gid)
        dispatch({ type: 'setLastWorkout', payload: res })
      } catch (error) {
        console.error('Error fetching data:', error)
      }
    }

    async function fetchCalendar() {
      try {
        const res = await getCalendar(id, 'gym')
        const activities = res.mesocycles.flatMap(mesocycle => mesocycle.microcycles).flatMap(microcycle => microcycle.activities)
        dispatch({ type: 'setActivities', payload: activities })
        
        if (activities.length === 0) return
        fetchLastWorkout(activities[activities.length - 1].id)
      } catch (error) {
        console.error('Error fetching data:', error)
      }
    }
    fetchCalendar()
  }, [])

  function handleAdd() {
    dispatch({ type: 'addWorkout' })
  }

  function handleClose() {
    dispatch({ type: 'closeWorkout' })
  }

  function handleOnSuccess() {
    window.location.reload()
  }

  if (state.activities === undefined) return <CircularProgress />

  return (
    <>
      <Workout
        lastWorkoutContent={
          <>
            {state.workout === undefined ? (
              <CircularProgress />
            ) : (
              state.workout.exercises.map(exercise => (
                <div className={styles.lastWorkoutExercise}>
                  <img src={`/images/no_image.svg`} alt="Exercise" />
                  <div className={styles.lastWorkoutExerciseContent}>
                    <h2>{exercise.name}</h2>
                    <ul>
                      {exercise.sets.map((set, idx) => (
                        <li>
                          Set {idx + 1}: {set.reps} x {set.weight} kg - {set.restTime}'
                        </li>
                      ))}
                    </ul>
                  </div>
                </div>
              ))
            )}
          </>
        }
        workouts={state.activities}
        onAdd={user.isCoach ? handleAdd : undefined}
      />

      {state.isOpen && <AddWorkout onClose={handleClose} onSuccess={handleOnSuccess} />}
    </>
  )
}
