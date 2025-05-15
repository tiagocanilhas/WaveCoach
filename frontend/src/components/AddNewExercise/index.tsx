import * as React from 'react'

import { TextField } from '@mui/material'
import { Button } from '../Button'
import { Popup } from '../Popup'

import { handleError } from '../../utils/handleError'

import { createGymExercise } from '../../services/gymExerciseServices'

import styles from './styles.module.css'

type State = {
  name: string
  error?: string
}

type Action = { type: 'setName'; name: string } | { type: 'setError'; error: string }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'setName':
      return { name: action.name, error: undefined }
    case 'setError':
      return { ...state, error: action.error }
    default:
      return state
  }
}

type AddNewExerciseProps = {
  category: string
  onClose: () => void
}

export function AddNewExercise({ category, onClose }: AddNewExerciseProps) {
  const [state, dispatch] = React.useReducer(reducer, { name: '', error: undefined })

  function handleOnChange(event: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'setName', name: event.target.value })
  }

  async function handleAddNewExercise() {
    const name = state.name.trim()
    try {
      await createGymExercise(name, category)
      onClose()
    } catch (error) {
      dispatch({ type: 'setError', error: handleError(error) })
    }
  }

  const exerciseName = state.name
  const disabled = exerciseName.length === 0

  return (
    <Popup
      title={`Add New ${category.charAt(0).toUpperCase() + category.slice(1)} Exercise`}
      content={
        <>
          <div className={styles.container}>
            <img src={`/images/no_image.svg`} alt="Exercise" />
            <TextField label="Exercise Name" name="name" value={exerciseName} onChange={handleOnChange} />
          </div>
          <Button text="Add" disabled={disabled} onClick={handleAddNewExercise} width="100%" height="30px" />
          {state.error && <p className={styles.error}>{state.error}</p>}
        </>
      }
      onClose={onClose}
    />
  )
}
