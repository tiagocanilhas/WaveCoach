import * as React from 'react'
import { useReducer } from 'react'
import { Navigate } from 'react-router-dom'

import { Card } from '../components/Card'

import { validatePassword } from '../utils/validatePassword'

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

const errorMessageStyle: React.CSSProperties = {
  color: 'red',
  fontSize: '14px',
}

type RegisterFormProps = {
  title: string
  initialUsername: string
  buttonText: string
  onSubmit: (username: string, password: string, confirmPassword: string) => Promise<void>
}

export function RegisterForm({ title, initialUsername, buttonText, onSubmit }: RegisterFormProps) {
  const [state, dispatch] = useReducer(reducer, {
    tag: 'editing',
    inputs: { username: initialUsername, password: '', confirmPassword: '' },
  })

  async function handleOnSubmit(ev: React.FormEvent<HTMLFormElement>) {
    ev.preventDefault()

    if (state.tag !== 'editing') return

    dispatch({ type: 'submit' })
    const { username, password, confirmPassword } = state.inputs

    try {
      await onSubmit(username, password, confirmPassword)
      dispatch({ type: 'success' })
    } catch (error) {
      dispatch({ type: 'error', error: 'An error occurred while registering. Please try again.' })
      return
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
          <h1>{title}</h1>
          <form onSubmit={handleOnSubmit}>
            <input name="username" type="text" placeholder="Username" value={username} onChange={handleOnChange} required />
            <input name="password" type="password" placeholder="Password" value={password} onChange={handleOnChange} required />
            <p>Password must match the following criteria:</p>
            <ul>
              {Object.values(passwordValidation).map((validation, idx) => (
                <li key={idx} style={{ color: validation.valid ? 'green' : 'grey' }}>
                  {validation.text}
                </li>
              ))}
            </ul>
            <input
              name="confirmPassword"
              type="password"
              placeholder="Confirm Password"
              value={confirmPassword}
              onChange={handleOnChange}
              required
            />
            <button type="submit" disabled={disabled}>
              {buttonText}
            </button>
          </form>
          {state.tag === 'editing' && state.error && <p style={errorMessageStyle}>{state.error}</p>}
        </>
      }
    />
  )
}
