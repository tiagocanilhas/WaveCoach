import * as React from 'react'
import { useReducer } from 'react'
import { Link, Navigate } from 'react-router-dom'

import { TextField } from '@mui/material'
import { Card } from '../../components/Card'
import { Button } from '../../components/Button'

import { login } from '../../services/userServices'

import { handleError } from '../../utils/handleError'

import { useAuthentication } from '../../hooks/useAuthentication'

import styles from './styles.module.css'

type State =
  | { tag: 'editing'; error?: string; inputs: { username: string; password: string } }
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
          return { tag: 'editing', error: action.error, inputs: { username: state.username, password: '' } }
        case 'success':
          return { tag: 'redirecting' }
        default:
          return state
      }

    case 'redirecting':
      return state
  }
}

const initialState: State = {
  tag: 'editing',
  inputs: { username: '', password: '' },
}

export function Login() {
  const [state, dispatch] = useReducer(reducer, initialState)
  const [_, setUser] = useAuthentication()

  async function handleOnSubmit(ev: React.FormEvent<HTMLFormElement>) {
    ev.preventDefault()

    if (state.tag !== 'editing') return

    dispatch({ type: 'submit' })
    const { username, password } = state.inputs

    try {
      const res = await login(username, password)
      setUser({ id: res.id, username: res.username })
      dispatch({ type: 'success' })
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error) })
      return
    }
  }

  function handleOnChange(ev: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', name: ev.currentTarget.name, value: ev.currentTarget.value })
  }

  if (state.tag === 'redirecting') return <Navigate to="/" />

  const username = state.tag === 'submitting' ? state.username : state.inputs.username
  const password = state.tag === 'submitting' ? '' : state.inputs.password
  const disabled = state.tag === 'submitting' || state.inputs.username.trim() === '' || state.inputs.password.trim() === ''

  return (
    <div className={styles.container}>
      <Card
        content={
          <>
            <h1 className={styles.title}>Login</h1>
            <form onSubmit={handleOnSubmit} className={styles.form}>
              <TextField name="username" type="text" label="Username" value={username} onChange={handleOnChange} required />
              <TextField name="password" type="password" label="Password" value={password} onChange={handleOnChange} required />
              <Button text="Login" disabled={disabled} type="submit" width="100%" height="25px" />
            </form>
            <p className={styles.text}>
              Don't have an account? <Link to="/register">Register</Link>
            </p>
            {state.tag === 'editing' && state.error && <p className={styles.errorMessage}>{state.error}</p>}
          </>
        }
      />
    </div>
  )
}
