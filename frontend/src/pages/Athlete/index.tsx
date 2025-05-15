import * as React from 'react'
import { useEffect, useReducer } from 'react'
import { useNavigate, useParams } from 'react-router-dom'

import { CircularProgress } from '@mui/material'
import { Activity } from '../../components/Activity'
import { Dropdown } from '../../components/Dropdown'
import { Card } from '../../components/Card'
import { ObjectList } from '../../components/ObjectList'
import { EditAthletePopup } from '../../components/EditAthletePopup'
import { Divisor } from '../../components/Divisor'
import { CyclesSelect } from '../../components/CyclesSelect'
import { Button } from '../../components/Button'
import { CyclesPopup } from '../../components/CyclesPopup'

import { Calendar } from '../../types/Calendar'
import { Athlete } from '../../types/Athlete'

import { epochConverter } from '../../utils/epochConverter'
import { epochConverterToAge } from '../../utils/epochConverterToAge'

import { useAuthentication } from '../../hooks/useAuthentication'

import { generateCode, getAthlete, deleteAthlete, getCalendar } from '../../services/athleteServices'

import styles from './styles.module.css'

type SelectedCycle = {
  mesocycleId: number
  microcycleId: number | null
} | null

type State = {
  athlete: Athlete | undefined
  calendar: Calendar | undefined
  cycleSelected: SelectedCycle
  isEditPopupOpen: boolean
  isCyclesPopupOpen: boolean
}

type Action =
  | { type: 'setAthlete'; athlete: Athlete }
  | { type: 'setCalendar'; calendar: Calendar }
  | { type: 'setCycleSelected'; cycleSelected: SelectedCycle }
  | { type: 'toggleEditPopup' }
  | { type: 'toggleCyclesPopup' }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'setAthlete':
      return { ...state, athlete: action.athlete }
    case 'setCalendar':
      return { ...state, calendar: action.calendar }
    case 'setCycleSelected':
      return { ...state, cycleSelected: action.cycleSelected }
    case 'toggleEditPopup':
      return { ...state, isEditPopupOpen: !state.isEditPopupOpen }
    case 'toggleCyclesPopup':
      return { ...state, isCyclesPopupOpen: !state.isCyclesPopupOpen }
    default:
      return state
  }
}

const initialState: State = {
  athlete: undefined,
  calendar: undefined,
  cycleSelected: null,
  isEditPopupOpen: false,
  isCyclesPopupOpen: false,
}

export function Athlete() {
  const [state, dispatch] = useReducer(reducer, initialState)
  const id = useParams().aid
  const [user, _] = useAuthentication()
  const navigate = useNavigate()

  useEffect(() => {
    async function fetchAthlete() {
      try {
        const res = await getAthlete(id)
        dispatch({ type: 'setAthlete', athlete: res })
      } catch (error) {
        console.error('Error fetching athlete:', error)
      }
    }
    async function fetchCalendar() {
      try {
        const res = await getCalendar(id)
        dispatch({ type: 'setCalendar', calendar: res })
      } catch (error) {
        console.error('Error fetching Activities:', error)
      }
    }

    fetchAthlete()
    fetchCalendar()
  }, [])

  async function handleGetCode() {
    try {
      const res = await generateCode(id)
      await navigator.clipboard.writeText(res.code)
      alert('Code was copied to clipboard!')
    } catch (error) {
      alert('Error generating code')
    }
  }

  function handleEdit() {
    dispatch({ type: 'toggleEditPopup' })
  }

  async function handleDelete() {
    if (confirm('Are you sure you want to delete this athlete?')) {
      try {
        await deleteAthlete(id)
        alert('Athlete deleted successfully')
        navigate('/')
      } catch (error) {
        alert('Error deleting athlete')
      }
    }
  }

  function onSuccess() {
    window.location.reload()
  }

  function handleCycleSelect(select: SelectedCycle) {
    dispatch({ type: 'setCycleSelected', cycleSelected: select })
  }

  function handleManageCycles() {
    dispatch({ type: 'toggleCyclesPopup' })
  }

  const athlete = state.athlete

  function getActivities() {
    if (state.calendar) {
      const mesocycles = state.calendar.mesocycles

      if (!state.cycleSelected) return mesocycles.flatMap(m => m.microcycles).flatMap(mc => mc.activities)

      const { mesocycleId, microcycleId } = state.cycleSelected

      if (microcycleId !== null) {
        const mesocycle = mesocycles.find(m => m.id === mesocycleId)
        const microcycle = mesocycle?.microcycles.find(mc => mc.id === microcycleId)
        return microcycle?.activities ?? []
      } else {
        const mesocycle = mesocycles.find(m => m.id === mesocycleId)
        return mesocycle?.microcycles.flatMap(mc => mc.activities) ?? []
      }
    }
  }

  const activities = getActivities()
  const cycleSelected = state.cycleSelected
  const isEditPopupOpen = state.isEditPopupOpen
  const isCyclesPopupOpen = state.isCyclesPopupOpen

  if (athlete === undefined || activities === undefined) {
    return <CircularProgress className={styles.waiting} />
  }

  return (
    <>
      <Divisor
        left={
          <div className={styles.left}>
            <Card
              content={
                <div className={styles.athlete}>
                  {user.isCoach && (
                    <Dropdown
                      options={[
                        { label: 'Generate Code', disabled: athlete.credentialsChanged, onClick: handleGetCode },
                        { label: 'Edit', onClick: handleEdit },
                        { label: 'Delete', onClick: handleDelete },
                      ]}
                    />
                  )}
                  <img src={'/images/anonymous-user.webp'} alt={athlete.name || 'Anonymous'} />
                  <h2>{athlete.name}</h2>
                  <p className={styles.age}>{epochConverterToAge(athlete.birthDate)} years</p>
                </div>
              }
              width="100%"
            />
            <Button text={user.isCoach ? 'Manage Cycles' : 'Calendar'} onClick={handleManageCycles} width="100%" height="40px" />
          </div>
        }
        right={
          <div className={styles.calendar}>
            <Card
              content={
                <CyclesSelect cycles={state.calendar.mesocycles} cycleSelected={cycleSelected} onSelect={handleCycleSelect} />
              }
              width="100%"
            />
            <ObjectList
              items={activities}
              getKey={activity => activity.id}
              renderItem={activity => <Activity activity={activity} />}
            />
          </div>
        }
      />

      {isEditPopupOpen && (
        <EditAthletePopup
          onClose={handleEdit}
          onSuccess={onSuccess}
          initialValues={{
            id: athlete.uid,
            name: athlete.name,
            birthdate: epochConverter(athlete.birthDate, 'yyyy-mm-dd'),
          }}
        />
      )}

      {isCyclesPopupOpen && <CyclesPopup onClose={handleManageCycles} onSuccess={onSuccess} cycles={state.calendar.mesocycles} />}
    </>
  )
}
