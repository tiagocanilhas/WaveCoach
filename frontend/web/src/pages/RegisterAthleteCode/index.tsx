import * as React from 'react'
import { Navigate, useNavigate } from 'react-router-dom'

import TextField from '@mui/material/TextField'
import { BackButton } from '../../components/BackButton'
import { Card } from '../../components/Card'
import { Button } from '../../components/Button'

import styles from './styles.module.css'

type State =
  | { tag: 'editing'; error?: string; code: string }
  | { tag: 'submitting'; code: string }
  | { tag: 'redirecting'; code: string }

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
          return { tag: 'editing', error: undefined, code: action.value }
        case 'submit':
          return { tag: 'submitting', code: state.code }
        default:
          return state
      }

    case 'submitting':
      switch (action.type) {
        case 'error':
          return { tag: 'editing', error: action.error, code: state.code }
        case 'success':
          return { tag: 'redirecting', code: state.code }
        default:
          return state
      }

    case 'redirecting':
      return state
  }
}

const initialState: State = {
  tag: 'editing',
  code: '',
}

export function RegisterAthleteCode() {
  const [state, dispatch] = React.useReducer(reducer, initialState)
  const navigate = useNavigate()

  function handleBack() {
    navigate('/register')
  }

  function handleOnSubmit(ev: React.FormEvent<HTMLFormElement>) {
    ev.preventDefault()

    if (state.tag !== 'editing') return

    dispatch({ type: 'submit' })

    dispatch({ type: 'success' })
  }

  function handleOnChange(ev: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', name: ev.currentTarget.name, value: ev.currentTarget.value })
  }

  if (state.tag === 'redirecting') return <Navigate to={`/register/athlete?code=${state.code}`} />

  const code = state.tag === 'editing' ? state.code : state.code
  const disabled = state.tag !== 'editing' || !state.code

  return (
    <div className={styles.container}>
      <Card
        content={
          <>
            <BackButton onClick={handleBack} />
            <h1 className={styles.title}>Check Code</h1>
            <form onSubmit={handleOnSubmit} className={styles.form}>
              <TextField name="code" type="text" label="Code" value={code} onChange={handleOnChange} required />
              <Button text="Check" type="submit" disabled={disabled} width="100%" height="25px" />
            </form>
            {state.tag === 'editing' && state.error && <p className={styles.error}>{state.error}</p>}
          </>
        }
      />
    </div>
  )
}
