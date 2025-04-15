import * as React from 'react'
import { useEffect } from 'react'
import { Link } from 'react-router-dom'

import { CircularProgress } from '@mui/material'
import { TextField } from '@mui/material'
import { Card } from '../../components/Card'
import { AddAthletePopup } from '../../components/AddAthletePopup'
import { ObjectListWithAdd } from '../../components/ObjectListWithAdd'
import { Dropdown } from '../../components/Dropdown'

import { getAthletes } from '../../services/athleteServices'

import { Athlete } from '../../types/athlete'

import styles from './styles.module.css'

type State = {
  popupOpen: boolean
  athletes?: Athlete[]
  search: string
}

type Action = { type: 'togglePopup' } | { type: 'setAthletes'; athletes: any[] } | { type: 'edit'; search: string }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'togglePopup':
      return { ...state, popupOpen: !state.popupOpen }
    case 'setAthletes':
      return { ...state, athletes: action.athletes }
    case 'edit':
      return { ...state, search: action.search }
    default:
      return state
  }
}

const initialState: State = {
  popupOpen: false,
  athletes: undefined,
  search: '',
}

export function Home() {
  const [state, dispatch] = React.useReducer(reducer, initialState)

  async function refetchAthletes() {
    try {
      const res = await getAthletes()
      dispatch({ type: 'setAthletes', athletes: res.athletes })
    } catch {
      dispatch({ type: 'setAthletes', athletes: [] })
    }
  }

  useEffect(() => {
    refetchAthletes()
  }, [])

  function handleSearch(ev: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', search: ev.target.value })
  }

  function handlePopup() {
    dispatch({ type: 'togglePopup' })
  }

  const athletes = state.athletes?.filter(athlete => {
    if (state.search === '') return true
    return athlete.name.toLowerCase().includes(state.search.toLowerCase())
  })

  if (state.athletes === undefined) {
    return <CircularProgress className={styles.waiting} />
  }

  return (
    <>
      <div className={styles.container}>
        <Card
          content={
            <TextField type="text" placeholder="Search" value={state.search} onChange={handleSearch} className={styles.search} />
          }
          width="500px"
        />

        <ObjectListWithAdd
          items={athletes}
          renderItem={athlete => (
            <div className={styles.athlete}>
              <Dropdown
                options={[
                  { label: 'Edit', onClick: () => console.log('Edit') },
                  { label: 'Delete', onClick: () => console.log('Delete') },
                ]}
              />
              <Link to={`/athletes/${athlete.uid}`} className={styles.link}>
                <img src={/*athlete.img || */ '/images/anonymous-user.webp'} alt={athlete.name || 'Anonymous'} />
              </Link>
              <h2 className={styles.name}>{athlete.name}</h2>
            </div>
          )}
          onAdd={handlePopup}
        />
      </div>

      <AddAthletePopup open={state.popupOpen} onClose={handlePopup} onSuccess={refetchAthletes} />
    </>
  )
}
