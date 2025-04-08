import * as React from 'react'
import { Navigate } from 'react-router-dom'

import { Card } from '../components/Card'

type State = { tag: 'editing'; error?: string; code: string } | { tag: 'submitting'; code: string } | { tag: 'redirecting' }

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
  code: '',
}

const errorMessageStyle: React.CSSProperties = {
  color: 'red',
  fontSize: '14px',
}

export function RegisterAthleteCode() {
  const [state, dispatch] = React.useReducer(reducer, initialState)

  async function handleOnSubmit(ev: React.FormEvent<HTMLFormElement>) {
    ev.preventDefault()

    if (state.tag !== 'editing') return

    dispatch({ type: 'submit' })

    try {
      const res = await (() => Promise.reject())()
      dispatch({ type: 'success' })
    } catch (error) {
      dispatch({ type: 'error', error: 'Invalid code' })
      return
    }
  }

  function handleOnChange(ev: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', name: ev.currentTarget.name, value: ev.currentTarget.value })
  }

  if (state.tag === 'redirecting') return <Navigate to="/register/athlete" />

  const code = state.tag === 'editing' ? state.code : state.code
  const disabled = state.tag !== 'editing' || !state.code

  return (
    <Card
      content={
        <>
          <h1>Check Code</h1>
          <form onSubmit={handleOnSubmit}>
            <input name="code" type="text" placeholder="Code" value={code} onChange={handleOnChange} required />
            <button type="submit" disabled={disabled}>
              Check
            </button>
          </form>
          {state.tag === 'editing' && state.error && <p style={errorMessageStyle}>{state.error}</p>}
        </>
      }
    />
  )
}
