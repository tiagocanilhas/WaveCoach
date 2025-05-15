import * as React from 'react'
import { useEffect, useReducer } from 'react'

import { CircularProgress, TextField } from '@mui/material'
import { ExerciseList } from '../ExerciseList'
import { Popup } from '../Popup'
import { AddNewExercise } from '../AddNewExercise'
import { AddExercise } from '../AddExercise'

import { Exercise } from '../../types/Exercise'
import { SetData } from '../../types/SetData'

import { getGymExercises } from '../../services/gymExerciseServices'

import styles from './styles.module.css'

type Type = 'chest' | 'back' | 'legs' | 'shoulders' | 'arms'

type ExerciseMap = Record<Type, Exercise[]>

function arrangeExercisesByType(exercises: Exercise[]): ExerciseMap {
  return exercises.reduce((acc, exercise) => {
    const type = exercise.category as Type

    if (!acc[type]) {
      acc[type] = []
    }

    acc[type].push(exercise)

    return acc
  }, {} as ExerciseMap)
}

type ViewTag =
  | { tag: 'loading' }
  | { tag: 'choosing' }
  | { tag: 'creating'; category: string }
  | { tag: 'adding'; exercise: Exercise }

type State = {
  exercises?: ExerciseMap | undefined
  view: ViewTag
}

type Action =
  | { type: 'choose' }
  | { type: 'new'; exerciseType: string }
  | { type: 'add'; exercise: Exercise }
  | { type: 'setData'; exercises: Exercise[] }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'setData':
      return { ...state, exercises: arrangeExercisesByType(action.exercises), view: { tag: 'choosing' } }
    case 'choose':
      return { ...state, view: { tag: 'choosing' } }
    case 'new':
      return { ...state, view: { tag: 'creating', category: action.exerciseType } }
    case 'add':
      return { ...state, view: { tag: 'adding', exercise: action.exercise } }
    default:
      return state
  }
}

type AddExercisePopupProps = {
  onAdd: (exercise: Exercise, sets: SetData[]) => void
  onClose: () => void
}

export function AddExercisePopup({ onAdd, onClose }: AddExercisePopupProps) {
  const initialState: State = { exercises: undefined, view: { tag: 'loading' } }
  const [state, dispatch] = useReducer(reducer, initialState)

  useEffect(() => {
    async function fetchData() {
      try {
        const res = await getGymExercises()
        dispatch({ type: 'setData', exercises: res.gymExercises })
      } catch (error) {}
    }
    fetchData()
  }, [])

  function openAddNewExercise(type: string) {
    dispatch({ type: 'new', exerciseType: type })
  }

  function closeAddNewExercise() {
    dispatch({ type: 'choose' })
  }

  function openAddExercise(exercise: Exercise) {
    dispatch({ type: 'add', exercise })
  }

  function closeAddExercise() {
    dispatch({ type: 'choose' })
  }

  return (
    <>
      <Popup
        title="Add Exercise"
        content={
          <div className={styles.addExercise}>
            <TextField label="Name" type="text" name="exercise" />
            <div className={styles.addExerciseContainer}>
              {state.view.tag === 'loading' ? (
                <CircularProgress />
              ) : (
                Object.entries(state.exercises).map(([category, items]) => (
                  <ExerciseList
                    key={category}
                    items={items}
                    category={category.charAt(0).toUpperCase() + category.slice(1)}
                    onAdd={openAddNewExercise}
                    onExerciseClick={openAddExercise}
                  />
                ))
              )}
            </div>
          </div>
        }
        onClose={onClose}
      />

      {state.view.tag === 'creating' && <AddNewExercise category={state.view.category} onClose={closeAddNewExercise} />}
      {state.view.tag === 'adding' && <AddExercise exercise={state.view.exercise} onAdd={onAdd} onClose={closeAddExercise} />}
    </>
  )
}
