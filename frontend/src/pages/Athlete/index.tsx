import * as React from 'react'
import { useEffect, useReducer, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'

import { CircularProgress } from '@mui/material'
import { Dropdown } from '../../components/Dropdown'
import { Card } from '../../components/Card'
import { ObjectList } from '../../components/ObjectList'
import { EditAthletePopup } from '../../components/EditAthletePopup'

import { Activity } from '../../types/activity'
import { Athlete } from '../../types/athlete'

import { epochConverter } from '../../utils/epochConverter'
import { epochConverterToAge } from '../../utils/epochConverterToAge'

import { generateCode } from '../../services/athleteServices'

import { getAthlete } from '../../services/athleteServices'
import { deleteAthlete } from '../../services/athleteServices'

import styles from './styles.module.css'

type State = {
  athlete: Athlete | undefined
  activities: Activity[] | undefined
  isEditPopupOpen: boolean
}

type Action =
  | { type: 'setAthlete'; athlete: Athlete }
  | { type: 'setActivities'; activities: Activity[] }
  | { type: 'toggleEditPopup' }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'setAthlete':
      return { ...state, athlete: action.athlete }
    case 'setActivities':
      return { ...state, activities: action.activities }
    case 'toggleEditPopup':
      return { ...state, isEditPopupOpen: !state.isEditPopupOpen }
    default:
      return state
  }
}

const initialState: State = {
  athlete: undefined,
  activities: [],
  isEditPopupOpen: false,
}

export function Athlete() {
  const navigate = useNavigate()
  const [state, dispatch] = useReducer(reducer, initialState)
  const id = useParams().aid

  useEffect(() => {
    async function fetchAthlete() {
      try {
        const res = await getAthlete(id)
        dispatch({ type: 'setAthlete', athlete: res })
      } catch (error) {
        console.error('Error fetching athlete:', error)
      }
    }
    async function fetchActivities() {
      try {
        const res = {
          activities: [
            { id: 1, date: 1744884381000 },
            { id: 2, date: 1744884382000 },
            { id: 3, date: 1744884383000 },
            { id: 4, date: 1744884384000 },
            { id: 5, date: 1744884385000 },
            { id: 6, date: 1744884386000 },
            { id: 7, date: 1744884387000 },
          ],
        }
        dispatch({ type: 'setActivities', activities: res.activities })
      } catch (error) {
        console.error('Error fetching Activities:', error)
      }
    }

    fetchAthlete()
    fetchActivities()
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

  async function handleEdit() {
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

  const athlete = state.athlete
  const activities = state.activities
  const isEditPopupOpen = state.isEditPopupOpen

  if (athlete === undefined || activities === undefined) {
    return <CircularProgress className={styles.waiting} />
  }

  return (
    <>
      <div className={styles.container}>
        <div className={styles.athleteCard}>
          <Card
            content={
              <div className={styles.athlete}>
                <Dropdown
                  options={[
                    { label: 'Generate Code', onClick: handleGetCode },
                    { label: 'Edit', onClick: handleEdit },
                    { label: 'Delete', onClick: handleDelete },
                  ]}
                />
                <img src={/*athlete.img ||*/ '/images/anonymous-user.webp'} alt={athlete.name || 'Anonymous'} />
                <h2 className={styles.name}>{athlete.name}</h2>
                <p className={styles.age}>{epochConverterToAge(athlete.birthDate)} years</p>
              </div>
            }
          />
        </div>

        <div className={styles.calendar}>
          <div className={styles.calendarContent}>
            <h2>Calendar</h2>
            <ObjectList
              items={activities}
              getKey={workout => workout.date}
              renderItem={workout => (
                <>
                  <h3>{epochConverter(workout.date, 'dd-mm-yyyy')}</h3>
                </>
              )}
            />
          </div>
        </div>
      </div>

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
    </>
  )
}
