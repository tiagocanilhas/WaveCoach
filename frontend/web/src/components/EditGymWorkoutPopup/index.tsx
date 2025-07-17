import * as React from 'react'
import { useReducer } from 'react'
import { useParams } from 'react-router-dom'

import { TextField } from '@mui/material'
import { SelectExercisePopup } from '../SelectExercisePopup'
import { Popup } from '../Popup'
import { Button } from '../Button'
import { ReorderableList } from '../ReorderableList'
import { EditExercisePopup } from '../EditExercisePopup'

import { Exercise } from '../../types/Exercise'
import { SetDataToAdd } from '../../types/SetDataToAdd'
import { GymWorkout } from '../../types/GymWorkout'
import { GymWorkoutExercise } from '../../types/GymWorkoutExercise'

import { updateGymActivity } from '../../../../services/gymServices'

import { handleError } from '../../../../utils/handleError'
import { epochConverter } from '../../../../utils/epochConverter'
import { WorkoutEditing } from '../../../../utils/WorkoutEditing'
import { diffListOrNull } from '../../../../utils/diffListOrNull'

import styles from './styles.module.css'

type State =
  | {
      tag: 'editing'
      isAdding: boolean
      date: string
      exercises: GymWorkoutExercise[]
      removedExercises: GymWorkoutExercise[]
      exerciseToEdit: GymWorkoutExercise
      error?: string
    }
  | {
      tag: 'submitting'
      isAdding: boolean
      date: string
      exercises: GymWorkoutExercise[]
      removedExercises: GymWorkoutExercise[]
      exerciseToEdit: GymWorkoutExercise
    }
  | { tag: 'submitted' }

type Action =
  | { type: 'toggleAdding' }
  | { type: 'setValue'; name: string; value: string }
  | { type: 'removeExercise'; id: number; tempId: number }
  | { type: 'addExercise'; exercise: GymWorkoutExercise }
  | { type: 'setExerciseToEdit'; exercise: GymWorkoutExercise }
  | { type: 'editExercise'; exercise: GymWorkoutExercise }
  | { type: 'setExercises'; exercises: GymWorkoutExercise[] }
  | { type: 'error'; error: string }
  | { type: 'submit' }
  | { type: 'success' }

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'editing':
      switch (action.type) {
        case 'toggleAdding':
          return { ...state, isAdding: !state.isAdding }
        case 'setValue':
          return { ...state, [action.name]: action.value }
        case 'removeExercise':
          const removedExercise = state.exercises.find(exercise =>
            exercise.id === null ? exercise.tempId === action.tempId : exercise.id === action.id
          )
          if (!removedExercise) return state

          return {
            ...state,
            exercises: state.exercises.filter(exercise =>
              exercise.id === null ? exercise.tempId !== action.tempId : exercise.id !== action.id
            ),
            removedExercises:
              removedExercise.id !== null
                ? [...state.removedExercises, WorkoutEditing.nullifyFieldsExceptId(removedExercise)]
                : state.removedExercises,
          }
        case 'addExercise':
          return {
            ...state,
            exercises: [...state.exercises, action.exercise],
            isAdding: false,
            exerciseToEdit: null,
          }
        case 'setExerciseToEdit':
          return {
            ...state,
            exerciseToEdit: action.exercise,
            isAdding: false,
          }
        case 'editExercise':
          return {
            ...state,
            exercises: state.exercises.map(exercise => {
              const matchById = exercise.id !== null && exercise.id === action.exercise.id
              const matchByTempId = exercise.id === null && exercise.tempId === action.exercise.tempId
              return matchById || matchByTempId ? action.exercise : exercise
            }),
            exerciseToEdit: null,
          }
        case 'setExercises':
          return { ...state, exercises: action.exercises }
        case 'error':
          return { ...state, error: action.error }
        case 'submit':
          return {
            tag: 'submitting',
            date: state.date,
            exercises: state.exercises,
            removedExercises: state.removedExercises,
            exerciseToEdit: state.exerciseToEdit,
            isAdding: state.isAdding,
          }
        default:
          return state
      }
    case 'submitting':
      switch (action.type) {
        case 'success':
          return { tag: 'submitted' }
        case 'error':
          return {
            tag: 'editing',
            isAdding: false,
            error: action.error,
            date: state.date,
            exercises: state.exercises,
            removedExercises: state.removedExercises,
            exerciseToEdit: state.exerciseToEdit,
          }
        default:
          return state
      }
    case 'submitted':
      return state
  }
}

type EditGymWorkoutPopupProps = {
  workout?: GymWorkout
  onClose: () => void
  onSuccess: () => void
}

export function EditGymWorkoutPopup({ workout, onClose, onSuccess }: EditGymWorkoutPopupProps) {
  const initialState: State = {
    tag: 'editing',
    isAdding: false,
    date: epochConverter(workout.date, 'yyyy-mm-dd'),
    exercises: JSON.parse(JSON.stringify(workout.exercises)) as GymWorkoutExercise[],
    removedExercises: [],
    exerciseToEdit: null,
    error: undefined,
  }
  const [state, dispatch] = useReducer(reducer, initialState)
  const gid = Number(useParams().gid)

  if (state.tag === 'submitted') {
    onSuccess()
    return
  }

  function handleToggleAdding() {
    dispatch({ type: 'toggleAdding' })
  }

  async function onAddExercise(exercise: Exercise, sets: SetDataToAdd[]) {
    dispatch({
      type: 'addExercise',
      exercise: {
        id: null,
        tempId: new Date().getTime(),
        gymExerciseId: exercise.id,
        name: exercise.name,
        url: exercise.url,
        sets: sets.map(set => ({
          id: null,
          tempId: new Date().getTime(),
          reps: set.reps,
          weight: set.weight,
          restTime: set.restTime,
        })),
      },
    })
  }

  function handleSetExerciseToEdit(exercise: GymWorkoutExercise) {
    dispatch({ type: 'setExerciseToEdit', exercise })
  }

  function handleOnSave(exercise: GymWorkoutExercise) {
    dispatch({ type: 'editExercise', exercise })
  }

  function handleOnDelete(id: number, tempId: number) {
    if (confirm('Are you sure you want to delete this exercise?')) dispatch({ type: 'removeExercise', id, tempId })
  }

  function handleReorder(exercises: GymWorkoutExercise[]) {
    dispatch({ type: 'setExercises', exercises })
  }

  function handleOnChange(event: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'setValue', name: event.target.name, value: event.target.value })
  }

  async function handleOnSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()

    if (state.tag !== 'editing') return

    dispatch({ type: 'submit' })

    const date = state.date === epochConverter(workout.date, 'yyyy-mm-dd') ? null : state.date

    const exercises =
      diffListOrNull(state.exercises, (exercise, index) => {
        const original = workout.exercises.find(e => e.id === exercise.id)

        const newExercise = {
          id: exercise.id,
          gymExerciseId: exercise.gymExerciseId,
          order: WorkoutEditing.checkOrder(index, exercise.order),
          sets: diffListOrNull(exercise.sets, (set, setIndex) => {
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

    try {
      await updateGymActivity(gid, date, [...exercises, ...state.removedExercises])
      dispatch({ type: 'success' })
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error.res) })
    }
  }

  const isAdding = state.isAdding
  const date = state.date
  const exercises = state.exercises
  const error = state.tag === 'editing' ? state.error : undefined
  const disabled =
    state.tag !== 'editing' ||
    state.exercises.length === 0 ||
    date.length === 0 ||
    (date === epochConverter(workout.date, 'yyyy-mm-dd') &&
      JSON.stringify(state.exercises) === JSON.stringify(initialState.exercises))

  return (
    <>
      <Popup
        title="Edit Workout"
        content={
          <>
            <form className={styles.addWorkout} onSubmit={handleOnSubmit}>
              <TextField type="date" name="date" value={date} onChange={handleOnChange} />
              <div className={styles.exercisesContainer}>
                <ReorderableList<GymWorkoutExercise>
                  list={exercises}
                  onReorder={handleReorder}
                  renderItem={item => (
                    <div className={styles.exercise}>
                      <div className={styles.exerciseInfo}>
                        <img src={item.url || `/images/no_image.svg`} alt="Exercise" />
                        <h3>{item.name}</h3>
                      </div>
                      <ul>
                        {item.sets.map((set, idx) => {
                          if (WorkoutEditing.checkDeleteObject(set)) return
                          return (
                            <li key={set.id || set.tempId}>
                              Set {idx + 1}: {set.reps} x {set.weight} kg - {set.restTime}'
                            </li>
                          )
                        })}
                      </ul>
                    </div>
                  )}
                  onClick={item => handleSetExerciseToEdit(item)}
                  onDelete={item => handleOnDelete(item.id, item.tempId)}
                  onAdd={handleToggleAdding}
                />
              </div>
              <Button text="Save" type="submit" disabled={disabled} width="100%" height="30px" />
            </form>
            {error && <p className={styles.error}>{error}</p>}
          </>
        }
        onClose={onClose}
      />

      {isAdding && <SelectExercisePopup onAdd={onAddExercise} onClose={handleToggleAdding} />}

      {state.exerciseToEdit && (
        <EditExercisePopup exercise={state.exerciseToEdit} onSave={handleOnSave} onClose={() => handleSetExerciseToEdit(null)} />
      )}
    </>
  )
}
