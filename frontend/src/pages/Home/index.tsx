import * as React from 'react'
import { useEffect, useReducer } from 'react'
import { Link, Navigate } from 'react-router-dom'

import { CircularProgress } from '@mui/material'
import { TextField } from '@mui/material'
import { Card } from '../../components/Card'
import { AddAthletePopup } from '../../components/AddAthletePopup'
import { ObjectList } from '../../components/ObjectList'
import { Dropdown } from '../../components/Dropdown'
import { ScrollableText } from '../../components/ScrollableText'

import { getAthletes } from '../../services/athleteServices'
import { generateCode } from '../../services/athleteServices'
import { deleteAthlete } from '../../services/athleteServices'

import { useAuthentication } from '../../hooks/useAuthentication'

import { Athlete } from '../../types/Athlete'

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
  const [state, dispatch] = useReducer(reducer, initialState)

  const [user] = useAuthentication()
  if (!user.isCoach) return <Navigate to={`/athletes/${user.id}`} />

  async function fetchAthletes() {
    try {
      const res = await getAthletes()
      dispatch({ type: 'setAthletes', athletes: res.athletes })
    } catch {
      dispatch({ type: 'setAthletes', athletes: [] })
    }
  }

  useEffect(() => {
    fetchAthletes()
  }, [])

  function handleSearch(ev: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', search: ev.target.value })
  }

  async function handleGetCode(id: number) {
    try {
      const res = await generateCode(id.toString())
      await navigator.clipboard.writeText(res.code)
      alert('Code was copied to clipboard!')
    } catch (error) {
      alert('Error generating code')
    }
  }

  async function handleDelete(id: number) {
    if (confirm('Are you sure you want to delete this athlete?')) {
      try {
        await deleteAthlete(id.toString())
        alert('Athlete deleted successfully')
        fetchAthletes()
      } catch (error) {
        alert('Error deleting athlete')
      }
    }
  }

  function handlePopup() {
    dispatch({ type: 'togglePopup' })
  }

  const athletes = state.athletes?.filter(athlete => {
    if (state.search === '') return true
    return athlete.name.toLowerCase().includes(state.search.toLowerCase())
  })

  const isPopupOpen = state.popupOpen

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

        <ObjectList
          items={athletes}
          getKey={athlete => athlete.uid}
          renderItem={athlete => (
            <div className={styles.athlete}>
              <Dropdown
                options={[
                  { label: 'Generate Code', disabled: athlete.credentialsChanged, onClick: () => handleGetCode(athlete.uid) },
                  { label: 'Delete', onClick: () => handleDelete(athlete.uid) },
                ]}
              />
              <Link to={`/athletes/${athlete.uid}`} className={styles.link}>
                <img src={'/images/anonymous-user.webp'} alt={athlete.name || 'Anonymous'} />
              </Link>
              <ScrollableText text={athlete.name} className={styles.name} />
            </div>
          )}
          onAdd={handlePopup}
        />
      </div>

      {isPopupOpen && <AddAthletePopup onClose={handlePopup} onSuccess={fetchAthletes} />}
    </>
  )
}
