import * as React from 'react'
import { useEffect } from 'react'

import { CircularProgress, TextField } from '@mui/material'
import { Card } from '../../components/Card'
import { Popup } from '../../components/Popup'
import { Dropdown } from '../../components/Dropdown'

import { handleError } from '../../utils/handleError'
import { createAthlete, getAthletes } from '../../services/athleteServices'
import styles from './styles.module.css'
import { Link } from 'react-router-dom'

type State =
  | {
      tag: 'editing'
      error?: string
      inputs: { name: string; birthdate: string }
      popupOpen: boolean
      athletes?: any[]
    }
  | {
      tag: 'submitting'
      name: string
      birthdate: string
      popupOpen: boolean
      athletes?: any[]
    }
  | {
      tag: 'submitted'
      popupOpen: boolean
      athletes?: any[]
    }

type Action =
  | { type: 'edit'; name: string; value: string }
  | { type: 'submit' }
  | { type: 'error'; error: string }
  | { type: 'success' }
  | { type: 'setAthletes'; athletes: any[] }
  | { type: 'togglePopup' }

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'editing':
      switch (action.type) {
        case 'edit':
          return {
            ...state,
            error: undefined,
            inputs: { ...state.inputs, [action.name]: action.value },
          }
        case 'submit':
          return {
            tag: 'submitting',
            name: state.inputs.name,
            birthdate: state.inputs.birthdate,
            popupOpen: state.popupOpen,
            athletes: state.athletes,
          }
        case 'togglePopup':
          return { ...state, popupOpen: !state.popupOpen }
        case 'setAthletes':
          return { ...state, athletes: action.athletes }
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
            popupOpen: state.popupOpen,
            athletes: state.athletes,
          }
        case 'success':
          return {
            tag: 'editing',
            inputs: { name: '', birthdate: '' },
            popupOpen: false,
            athletes: state.athletes,
          }
        default:
          return state
      }

    case 'submitted':
      return state
  }
}

const initialState: State = {
  tag: 'editing',
  inputs: { name: '', birthdate: '' },
  popupOpen: false,
  athletes: undefined,
}

export function Home() {
  const [state, dispatch] = React.useReducer(reducer, initialState)

  useEffect(() => {
    async function fetchAthletes() {
      try {
        const res = await getAthletes()
        dispatch({ type: 'setAthletes', athletes: res.athletes })
      } catch (error) {
        dispatch({ type: 'setAthletes', athletes: [] })
      }
    }

    fetchAthletes()
  }, [])

  function handlePopup() {
    dispatch({ type: 'togglePopup' })
  }

  const handleOnSubmit = async (ev: React.FormEvent<HTMLFormElement>) => {
    ev.preventDefault()
    if (state.tag !== 'editing') return

    dispatch({ type: 'submit' })

    const { name, birthdate } = state.inputs

    try {
      await createAthlete(name, birthdate)
      dispatch({ type: 'success' })
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error) })
      return
    }
  }

  const handleOnChange = (ev: React.ChangeEvent<HTMLInputElement>) => {
    dispatch({ type: 'edit', name: ev.currentTarget.name, value: ev.currentTarget.value })
  }

  const name = state.tag === 'editing' ? state.inputs.name : ''
  const birthdate = state.tag === 'editing' ? state.inputs.birthdate : ''
  const disabled = state.tag === 'submitting' || name.trim() === '' || birthdate.trim() === ''

  return state.athletes === undefined ? (
    <div className={styles.waiting}>
      <CircularProgress />
    </div>
  ) : (
    <>
      <div className={styles.container}>
        <Card
          content={
            <div className={styles.addAthlete} onClick={handlePopup}>
              +
            </div>
          }
          width="200px"
          height="200px"
        />
        {state.athletes.length > 0 &&
          state.athletes.map(athlete => (
            <Card
              key={athlete.uid}
              content={
                <>
                  <div className={styles.athlete}>
                    <Dropdown options={['Edit', 'Delete']} />
                    <Link to={`/athletes/${athlete.uid}`} className={styles.link}>
                      {athlete.img ? (
                        <img src={athlete.img} alt={athlete.name} />
                      ) : (
                        <img src={'/images/anonymous-user.webp'} alt="Anonymous" />
                      )}
                    </Link>
                    <h2 className={styles.name}>{athlete.name}</h2>
                  </div>
                </>
              }
              width="200px"
              height="200px"
            />
          ))}
      </div>

      {state.popupOpen && (
        <Popup
          title="Add Athlete"
          content={
            <form onSubmit={handleOnSubmit} className={styles.form}>
              <TextField name="name" type="text" label="Name" value={name} onChange={handleOnChange} required />
              <TextField name="birthdate" type="date" value={birthdate} onChange={handleOnChange} required />
              <button type="submit" disabled={disabled} className={styles.btn}>
                Add
              </button>
              {state.tag === 'editing' && state.error && <div className={styles.error}>{state.error}</div>}
            </form>
          }
          onClose={handlePopup}
        />
      )}
    </>
  )
}
