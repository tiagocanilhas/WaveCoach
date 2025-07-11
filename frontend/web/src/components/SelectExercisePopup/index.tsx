import * as React from 'react'
import { useEffect, useReducer } from 'react'

import { CircularProgress, TextField } from '@mui/material'
import { ExerciseList } from '../ExerciseList'
import { Popup } from '../Popup'
import { AddNewExercise } from '../AddNewExercise'
import { AddExercisePopup } from '../AddExercisePopup'

import { Exercise } from '../../types/Exercise'
import { SetDataToAdd } from '../../types/SetDataToAdd'

import { getGymExercises } from '../../../../services/gymExerciseServices'

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
  name: string
  exercises?: ExerciseMap | undefined
  view: ViewTag
}

type Action =
  | { type: 'choose' }
  | { type: 'new'; exerciseType: string }
  | { type: 'add'; exercise: Exercise }
  | { type: 'SetDataToAdd'; exercises: Exercise[] }
  | { type: 'setName'; name: string }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'SetDataToAdd':
      return { ...state, exercises: arrangeExercisesByType(action.exercises), view: { tag: 'choosing' } }
    case 'choose':
      return { ...state, view: { tag: 'choosing' } }
    case 'new':
      return { ...state, view: { tag: 'creating', category: action.exerciseType } }
    case 'add':
      return { ...state, view: { tag: 'adding', exercise: action.exercise } }
    case 'setName':
      return { ...state, name: action.name }
    default:
      return state
  }
}

type SelectExercisePopupProps = {
  onAdd: (exercise: Exercise, sets: SetDataToAdd[]) => void
  onClose: () => void
}

export function SelectExercisePopup({ onAdd, onClose }: SelectExercisePopupProps) {
  const initialState: State = { name: '', exercises: undefined, view: { tag: 'loading' } }
  const [state, dispatch] = useReducer(reducer, initialState)

  useEffect(() => {
    async function fetchData() {
      try {
        const { status, res } = await getGymExercises()
        dispatch({ type: 'SetDataToAdd', exercises: res.gymExercises })
      } catch (error) {}
    }
    fetchData()
  }, [])

  function handleOnChange(e: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'setName', name: e.target.value })
  }

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
            <TextField label="Name" type="text" name="exercise" onChange={handleOnChange} value={state.name} />
            <div className={styles.addExerciseContainer}>
              {state.view.tag === 'loading' ? (
                <CircularProgress />
              ) : (
                Object.entries(state.exercises).map(([category, items]) => {
                  const filteredItems = items.filter(e => e.name.toLowerCase().includes(state.name.toLowerCase()))

                  if (filteredItems.length === 0) return null

                  return (
                    <ExerciseList
                      key={category}
                      items={filteredItems}
                      category={category.charAt(0).toUpperCase() + category.slice(1)}
                      onAdd={openAddNewExercise}
                      onExerciseClick={openAddExercise}
                    />
                  )
                })
              )}
            </div>
          </div>
        }
        onClose={onClose}
      />

      {state.view.tag === 'creating' && <AddNewExercise category={state.view.category} onClose={closeAddNewExercise} />}
      {state.view.tag === 'adding' && (
        <AddExercisePopup exercise={state.view.exercise} onAdd={onAdd} onClose={closeAddExercise} />
      )}
    </>
  )
}
