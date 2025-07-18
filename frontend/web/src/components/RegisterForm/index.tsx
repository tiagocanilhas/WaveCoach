import * as React from 'react'
import { useReducer } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'

import TextField from '@mui/material/TextField'
import { Card } from '../Card'
import { Button } from '../Button'
import { BackButton } from '../BackButton'

import { validatePassword } from '../../../../utils/validatePassword'
import { handleError } from '../../../../utils/handleError'

import styles from './styles.module.css'

type State =
  | { tag: 'editing'; error?: string; inputs: { username: string; password: string; confirmPassword: string } }
  | { tag: 'submitting'; username: string }
  | { tag: 'redirecting' }

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
          return { tag: 'editing', error: undefined, inputs: { ...state.inputs, [action.name]: action.value } }
        case 'submit':
          return { tag: 'submitting', username: state.inputs.username }
        default:
          return state
      }

    case 'submitting':
      switch (action.type) {
        case 'error':
          return { tag: 'editing', error: action.error, inputs: { username: state.username, password: '', confirmPassword: '' } }
        case 'success':
          return { tag: 'redirecting' }
        default:
          return state
      }

    case 'redirecting':
      return state
  }
}

type RegisterFormProps = {
  title: string
  initialUsername: string
  buttonText: string
  onSubmit: (username: string, password: string) => Promise<void>
}

export function RegisterForm({ title, initialUsername, buttonText, onSubmit }: RegisterFormProps) {
  const initialState: State = { tag: 'editing', inputs: { username: initialUsername, password: '', confirmPassword: '' } }
  const [state, dispatch] = useReducer(reducer, initialState)
  const navigate = useNavigate()

  function handleBack() {
    navigate('/register')
  }

  async function handleOnSubmit(ev: React.FormEvent<HTMLFormElement>) {
    ev.preventDefault()

    if (state.tag !== 'editing') return

    dispatch({ type: 'submit' })
    const { username, password } = state.inputs

    try {
      await onSubmit(username, password)
      dispatch({ type: 'success' })
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error.res) })
    }
  }

  function handleOnChange(ev: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', name: ev.currentTarget.name, value: ev.currentTarget.value })
  }

  if (state.tag === 'redirecting') return <Navigate to="/login" />

  const username = state.tag === 'submitting' ? state.username : state.inputs.username
  const password = state.tag === 'submitting' ? '' : state.inputs.password
  const confirmPassword = state.tag === 'submitting' ? '' : state.inputs.confirmPassword

  const passwordValidation = validatePassword(password)
  const passwordsMatch = password === confirmPassword
  const passwordError = password.length > 0 && !Object.values(passwordValidation).every(v => v.valid)
  const confirmPasswordError = confirmPassword.length > 0 && !passwordsMatch
  const disabled =
    state.tag === 'submitting' ||
    state.inputs.username.trim() === '' ||
    state.inputs.password.trim() === '' ||
    state.inputs.confirmPassword.trim() === '' ||
    !passwordValidation ||
    !passwordsMatch ||
    !Object.values(passwordValidation).every(v => v.valid)

  return (
    <Card
      content={
        <>
          <BackButton onClick={handleBack} />
          <h1 className={styles.title}>{title}</h1>
          <form onSubmit={handleOnSubmit} className={styles.form}>
            <TextField name="username" type="text" label="Username" value={username} onChange={handleOnChange} required />
            <div className={styles.passwordContainer}>
              <TextField
                name="password"
                type="password"
                label="Password"
                value={password}
                onChange={handleOnChange}
                error={passwordError}
                helperText={
                  <ul>
                    {Object.values(passwordValidation).map((validation, idx) => (
                      <li key={idx} className={validation.valid ? styles.passwordCriteriaValid : styles.passwordCriteriaInvalid}>
                        {validation.text}
                      </li>
                    ))}
                  </ul>
                }
                required
              />
            </div>
            <TextField
              name="confirmPassword"
              type="password"
              label="Confirm Password"
              value={confirmPassword}
              onChange={handleOnChange}
              error={confirmPasswordError}
              required
            />
            <Button text={buttonText} type="submit" width="100%" height="25px" disabled={disabled} />
          </form>
          {state.tag === 'editing' && state.error && <p className={styles.errorMessage}>{state.error}</p>}
        </>
      }
    />
  )
}
