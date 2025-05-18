import * as React from 'react'
import { useReducer } from 'react'

import { TextField } from '@mui/material'
import { Card } from '../Card'
import { AddExercisePopup } from '../AddExercisePopup'
import { Popup } from '../Popup'
import { Button } from '../Button'

import { Exercise } from '../../types/Exercise'
import { SetData } from '../../types/SetData'

import styles from './styles.module.css'
import { createGymActivity } from '../../services/gymServices'
import { useParams } from 'react-router-dom'

type State = {
  isOpen: boolean
  date: string
  exercises: any[]
  error?: string
}

type Action =
  | { type: 'openPopup' }
  | { type: 'closePopup' }
  | { type: 'setDate'; date: string }
  | { type: 'addExercise'; payload: { exercise: Exercise; sets: SetData[] } }
  | { type: 'removeExercise'; id: number }
  | { type: 'error'; error: string }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'openPopup':
      return { ...state, isOpen: true }
    case 'closePopup':
      return { ...state, isOpen: false }
    case 'setDate':
      return { ...state, date: action.date }
    case 'addExercise':
      return {
        ...state,
        isOpen: false,
        exercises: [...state.exercises, action.payload],
      }
    case 'removeExercise':
      return {
        ...state,
        exercises: state.exercises.filter(info => info.exercise.id !== action.id),
      }
    case 'error':
      return { ...state, error: action.error }
    default:
      return state
  }
}

type AddWorkoutProps = {
  onClose: () => void
  onSuccess: () => void
}

const initialState: State = {
  date: new Date().toISOString().split('T')[0],
  isOpen: false,
  exercises: [],
}

export function AddWorkout({ onClose, onSuccess }: AddWorkoutProps) {
  const [state, dispatch] = useReducer(reducer, initialState)
  const id = useParams().aid

  function handleAddExercise() {
    dispatch({ type: 'openPopup' })
  }

  function handleCloseExercise() {
    dispatch({ type: 'closePopup' })
  }

  function onAddExercise(exercise: Exercise, sets: SetData[]) {
    dispatch({ type: 'addExercise', payload: { exercise, sets } })
  }

  function handleOnChange(event: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'setDate', date: event.target.value })
  }

  async function handleOnClick() {
    try {
      const exercises = state.exercises.map(info => ({
        id: info.exercise.id,
        sets: info.sets.map(set => ({
          reps: set.reps,
          weight: set.weight,
          restTime: set.restTime,
        })),
      }))
      await createGymActivity(id, state.date, exercises)
      onSuccess()
    } catch (error) {
      console.error('Error adding workout:', error)
    }
  }

  const isOpen = state.isOpen
  const date = state.date
  const exercises = state.exercises
  const disabled = state.exercises.length === 0 || date.length === 0

  return (
    <>
      <Popup
        title="Add Workout"
        content={
          <>
            <div className={styles.addWorkout}>
              <TextField type="date" name="date" value={date} onChange={handleOnChange} />
              <div className={styles.exercisesContainer}>
                {exercises.map(info => (
                  <Card
                    content={
                      <div className={styles.exercise}>
                        <div className={styles.remove} onClick={() => dispatch({ type: 'removeExercise', id: info.exercise.id })}>
                          🗑️
                        </div>
                        <div className={styles.exerciseInfo}>
                          <img src={`/images/no_image.svg`} alt="Exercise" />
                          <h3>{info.exercise.name}</h3>
                        </div>
                        <ul>
                          {info.sets.map((set, idx) => (
                            <li>
                              Set {idx + 1}: {set.reps} x {set.weight} kg - {set.restTime}'
                            </li>
                          ))}
                        </ul>
                      </div>
                    }
                    width="600px"
                  />
                ))}
                <Card
                  content={
                    <div className={styles.add} onClick={handleAddExercise}>
                      +
                    </div>
                  }
                  width="600px"
                />
              </div>
              <Button text="Add" onClick={handleOnClick} disabled={disabled} width="100%" height="30px" />
            </div>
            {state.error && <p className={styles.error}>{state.error}</p>}
          </>
        }
        onClose={onClose}
      />

      {isOpen && <AddExercisePopup onAdd={onAddExercise} onClose={handleCloseExercise} />}
    </>
  )
}
