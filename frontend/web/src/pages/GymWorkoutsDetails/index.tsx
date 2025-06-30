import * as React from 'react'
import { useEffect, useState } from 'react'
import { Navigate, useParams } from 'react-router-dom'

import { CircularProgress } from '@mui/material'
import { Card } from '../../components/Card'
import { Button } from '../../components/Button'
import { EditGymWorkoutPopup } from '../../components/EditGymWorkoutPopup'

import { GymWorkout } from '../../types/GymWorkout'

import { getGymActivity } from '../../../../services/gymServices'

import { epochConverter } from '../../../../utils/epochConverter'

import styles from './styles.module.css'

type State = { tag: 'loading' } | { tag: 'loaded'; workout: GymWorkout; isEditing: boolean } | { tag: 'error' }

type Action = { type: 'setWorkout'; workout: GymWorkout } | { type: 'toggleEditing' } | { type: 'setError' }

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'loading':
      switch (action.type) {
        case 'setWorkout':
          return { tag: 'loaded', workout: action.workout, isEditing: false }
        case 'setError':
          return { tag: 'error' }
        default:
          return state
      }
    case 'loaded':
      switch (action.type) {
        case 'toggleEditing':
          return { ...state, isEditing: !state.isEditing }
        case 'setWorkout':
          return { ...state, workout: action.workout }
        default:
          return state
      }
    case 'error':
      return state
  }
}

export function GymWorkoutsDetails() {
  const [state, dispatch] = React.useReducer(reducer, { tag: 'loading' })
  const gid = useParams().gid

  async function fetchData() {
    try {
      const res = await getGymActivity(gid)
      dispatch({ type: 'setWorkout', workout: res })
    } catch (error) {
      dispatch({ type: 'setError' })
    }
  }

  useEffect(() => {
    fetchData()
  }, [])

  function handleEdit() {
    dispatch({ type: 'toggleEditing' })
  }

  function handleOnSuccess() {
    fetchData()
    dispatch({ type: 'toggleEditing' })
  }

  const workout = state.tag === 'loaded' ? state.workout : null
  const isEditing = state.tag === 'loaded' ? state.isEditing : false

  if (state.tag === 'loading') return <CircularProgress />

  if (state.tag === 'error') return <Navigate to="/error" replace />

  return (
    <>
      <div className={styles.container}>
        <div className={styles.header}>
          <h2>{epochConverter(workout.date, 'dd-mm-yyyy')}</h2>
          <Button text="Edit" onClick={handleEdit} />
        </div>
        {workout.exercises.map(exercise => (
          <Card
            key={exercise.id}
            content={
              <div className={styles.exercise}>
                <div className={styles.exerciseInfo}>
                  <img src={exercise.url || `/images/no_image.svg`} alt="Exercise" />
                  <h3>{exercise.name}</h3>
                </div>
                <ul>
                  {exercise.sets.map((set, idx) => (
                    <li key={set.id}>
                      Set {idx + 1}: {set.reps} x {set.weight} kg - {set.restTime}'
                    </li>
                  ))}
                </ul>
              </div>
            }
            width="600px"
          />
        ))}
      </div>

      {isEditing && <EditGymWorkoutPopup workout={workout} onClose={handleEdit} onSuccess={handleOnSuccess} />}
    </>
  )
}
