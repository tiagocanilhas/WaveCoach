import * as React from 'react'
import { useReducer } from 'react'

import { TextField } from '@mui/material'
import { Card } from '../Card'
import { SelectExercisePopup } from '../SelectExercisePopup'
import { Popup } from '../Popup'
import { Button } from '../Button'

import { Exercise } from '../../types/Exercise'
import { SetData } from '../../types/SetData'

import styles from './styles.module.css'
import { createGymActivity } from '../../../../services/gymServices'
import { useParams } from 'react-router-dom'
import { handleError } from '../../../../utils/handleError'
import { ExerciseToAdd } from '../../types/ExerciseToAdd'
import { VerticalReorderableList } from '../VerticalReordabelList'
import { AddExercisePopup } from '../AddExercisePopup'

type State = {
  isAdding: boolean
  date: string
  exercises: ExerciseToAdd[]
  exerciseToEdit: ExerciseToAdd | null
  error?: string
}

type Action =
  | { type: 'toggleAdding' }
  | { type: 'setDate'; date: string }
  | { type: 'addExercise'; exercise: ExerciseToAdd }
  | { type: 'setExerciseToEdit'; exercise: ExerciseToAdd }
  | { type: 'updateExercise'; exercise: ExerciseToAdd }
  | { type: 'removeExercise'; id: string }
  | { type: 'setExercises'; exercises: ExerciseToAdd[] }
  | { type: 'error'; error: string }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'toggleAdding':
      return { ...state, isAdding: !state.isAdding }
    case 'setDate':
      return { ...state, date: action.date }
    case 'addExercise':
      return {
        ...state,
        isAdding: false,
        exercises: [...state.exercises, action.exercise],
      }
    case 'setExerciseToEdit':
      return { ...state, exerciseToEdit: action.exercise }
    case 'updateExercise':
      return {
        ...state,
        exercises: state.exercises.map(info => (info.exercise.id === action.exercise.exercise.id ? action.exercise : info)),
        exerciseToEdit: null,
      }
    case 'removeExercise':
      return {
        ...state,
        exercises: state.exercises.filter(info => info.tempId !== action.id),
      }
    case 'setExercises':
      return { ...state, exercises: action.exercises }
    case 'error':
      return { ...state, error: action.error }
    default:
      return state
  }
}

type AddGymWorkoutPopupProps = {
  onClose: () => void
  onSuccess: () => void
}

const initialState: State = {
  isAdding: false,
  date: new Date().toISOString().split('T')[0],
  exercises: [],
  exerciseToEdit: null,
  error: undefined,
}

export function AddGymWorkoutPopup({ onClose, onSuccess }: AddGymWorkoutPopupProps) {
  const [state, dispatch] = useReducer(reducer, initialState)
  const id = useParams().aid

  function handleToggleAdding() {
    dispatch({ type: 'toggleAdding' })
  }

  function onAddExercise(exercise: Exercise, sets: SetData[]) {
    dispatch({ type: 'addExercise', exercise: { tempId: Date.now().toString(), exercise, sets } })
  }

  function handleSetExerciseToEdit(exercise: ExerciseToAdd) {
    dispatch({ type: 'setExerciseToEdit', exercise })
  }

  function handleUpdateExercise(exercise: Exercise, sets: SetData[]) {
    dispatch({ type: 'updateExercise', exercise: { ...state.exerciseToEdit, exercise, sets } })
  }

  function handleOnDelete(id: string) {
    dispatch({ type: 'removeExercise', id })
  }

  function handleReorder(exercises: ExerciseToAdd[]) {
    dispatch({ type: 'setExercises', exercises })
  }

  function handleOnChange(event: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'setDate', date: event.target.value })
  }

  async function handleOnSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()

    const date = state.date
    const exercises = state.exercises.map(info => ({
      gymExerciseId: info.exercise.id,
      sets: info.sets.map(set => ({
        reps: set.reps,
        weight: set.weight,
        restTime: set.restTime,
      })),
    }))

    try {
      await createGymActivity(id, date, exercises)
      onSuccess()
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error) })
    }
  }

  const isAdding = state.isAdding
  const date = state.date
  const exercises = state.exercises
  const error = state.error
  const disabled = state.exercises.length === 0 || date.length === 0

  return (
    <>
      <Popup
        title="Add Workout"
        content={
          <>
            <form className={styles.addWorkout} onSubmit={handleOnSubmit}>
              <TextField type="date" name="date" value={date} onChange={handleOnChange} />
              <div className={styles.exercisesContainer}>
                <VerticalReorderableList<ExerciseToAdd>
                  list={exercises}
                  onReorder={handleReorder}
                  renderItem={item => (
                    <div className={styles.exercise}>
                      <div className={styles.exerciseInfo}>
                        <img src={item.exercise.url || `/images/no_image.svg`} alt="Exercise" />
                        <h3>{item.exercise.name}</h3>
                      </div>
                      <ul>
                        {item.sets.map((set, idx) => (
                          <li key={idx}>
                            Set {idx + 1}: {set.reps} x {set.weight} kg - {set.restTime}'
                          </li>
                        ))}
                      </ul>
                    </div>
                  )}
                  onClick={item => handleSetExerciseToEdit(item)}
                  onDelete={item => handleOnDelete(item.tempId)}
                  onAdd={handleToggleAdding}
                />
              </div>
              <Button text="Add" type="submit" disabled={disabled} width="100%" height="30px" />
            </form>
            {state.error && <p className={styles.error}>{error}</p>}
          </>
        }
        onClose={onClose}
      />

      {isAdding && <SelectExercisePopup onAdd={onAddExercise} onClose={handleToggleAdding} />}

      {state.exerciseToEdit && (
        <AddExercisePopup
          data={state.exerciseToEdit}
          exercise={state.exerciseToEdit.exercise}
          onAdd={handleUpdateExercise}
          onClose={() => handleSetExerciseToEdit(null)}
        />
      )}
    </>
  )
}
