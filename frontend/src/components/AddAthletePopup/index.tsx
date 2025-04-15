import * as React from 'react'
import { TextField } from '@mui/material'
import { Popup } from '../../components/Popup'
import styles from './styles.module.css'
import { createAthlete } from '../../services/athleteServices'
import { handleError } from '../../utils/handleError'
import { useReducer } from 'react'

type State =
  | { tag: 'editing'; inputs: { name: string; birthdate: string }; error?: string }
  | { tag: 'submitting'; name: string; birthdate: string }
  | { tag: 'submitted' }

type Action =
  | { type: 'edit'; name: string; value: string }
  | { type: 'submit' }
  | { type: 'error'; error: string }
  | { type: 'success' }

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'editing':
      switch (action.type) {
        case 'edit':
          return { ...state, inputs: { ...state.inputs, [action.name]: action.value }, error: undefined }
        case 'submit':
          return { tag: 'submitting', name: state.inputs.name, birthdate: state.inputs.birthdate }
        default:
          return state
      }

    case 'submitting':
      switch (action.type) {
        case 'error':
          return {
            tag: 'editing',
            error: action.error,
            inputs: { name: state.name, birthdate: state.birthdate },
          }
        case 'success':
          return {
            tag: 'submitted',
          }
        default:
          return state
      }

    case 'submitted':
      return {
        tag: 'editing',
        inputs: { name: '', birthdate: '' },
      }
  }
}

type AddAthletePopupProps = {
  open: boolean
  onClose: () => void
  onSuccess: () => void
}

const initialState: State = {
  tag: 'editing',
  inputs: { name: '', birthdate: '' },
}

export function AddAthletePopup({ open, onClose, onSuccess }: AddAthletePopupProps) {
  const [state, dispatch] = useReducer(reducer, initialState)

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    if (state.tag !== 'editing') return

    dispatch({ type: 'submit' })

    try {
      await createAthlete(state.inputs.name, state.inputs.birthdate)
      dispatch({ type: 'success' })
      onSuccess()
      onClose()
    } catch (err) {
      dispatch({ type: 'error', error: handleError(err) })
    }
  }

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', name: e.target.name, value: e.target.value })
  }

  const name = state.tag === 'editing' ? state.inputs.name : ''
  const birthdate = state.tag === 'editing' ? state.inputs.birthdate : ''
  const disabled = state.tag === 'submitting' || name.trim() === '' || birthdate.trim() === ''

  if (!open) return null

  return (
    <Popup
      title="Add Athlete"
      onClose={onClose}
      content={
        <form onSubmit={handleSubmit} className={styles.form}>
          <TextField name="name" label="Name" value={name} onChange={handleChange} required />
          <TextField name="birthdate" type="date" value={birthdate} onChange={handleChange} required />
          <button type="submit" disabled={disabled} className={styles.btn}>
            Add
          </button>
          {state.tag === 'editing' && state.error && <div className={styles.error}>{state.error}</div>}
        </form>
      }
    />
  )
}
