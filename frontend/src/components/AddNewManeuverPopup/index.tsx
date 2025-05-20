import * as React from 'react'

import { TextField } from '@mui/material'
import { Button } from '../Button'
import { Popup } from '../Popup'

import { handleError } from '../../utils/handleError'

import { createWaterManeuver } from '../../services/waterManeuverServices'

import styles from './styles.module.css'
import { useReducer } from 'react'

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

type AddNewManeuverProps = {
  onClose: () => void
  onSuccess: () => void
}

export function AddNewManeuver({ onClose, onSuccess }: AddNewManeuverProps) {
  const [state, dispatch] = useReducer(reducer, { name: '', error: undefined })

  function handleOnChange(event: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'setName', name: event.target.value })
  }

  async function handleAddNewManeuver() {
    const name = state.name.trim()
    try {
      await createWaterManeuver(name)
      onSuccess()
    } catch (error) {
      dispatch({ type: 'setError', error: handleError(error) })
    }
  }

  const exerciseName = state.name
  const disabled = exerciseName.length === 0

  return (
    <Popup
      title="Add New Maneuver"
      content={
        <>
          <TextField label="Maneuver Name" name="name" value={exerciseName} onChange={handleOnChange} />
          <Button text="Add" disabled={disabled} onClick={handleAddNewManeuver} width="100%" height="30px" />
          {state.error && <p className={styles.error}>{state.error}</p>}
        </>
      }
      onClose={onClose}
    />
  )
}
