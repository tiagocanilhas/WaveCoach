import * as React from 'react'
import { useEffect, useReducer, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'

import { CircularProgress } from '@mui/material'
import { Dropdown } from '../../components/Dropdown'
import { Card } from '../../components/Card'
import { ObjectList } from '../../components/ObjectList'
import { EditAthletePopup } from '../../components/EditAthletePopup'
import { Divisor } from '../../components/Divisor'

import { Activity } from '../../types/activity'
import { Athlete } from '../../types/athlete'

import { epochConverter } from '../../utils/epochConverter'
import { epochConverterToAge } from '../../utils/epochConverterToAge'

import { generateCode } from '../../services/athleteServices'

import { getAthlete } from '../../services/athleteServices'
import { deleteAthlete } from '../../services/athleteServices'
import { getActivities } from '../../services/athleteServices'

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
        const res = await getActivities(id)
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
      <Divisor
        left={
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
                <h2>{athlete.name}</h2>
                <p className={styles.age}>{epochConverterToAge(athlete.birthDate)} years</p>
              </div>
            }
            width="100%"
          />
        }
        right={
          <div className={styles.calendar}>
            <ObjectList
              items={activities}
              getKey={activity => activity.id}
              renderItem={activity => (
                <div className={styles.activity}>
                  <div className={styles.imageContainer}>
                    <img 
                      src={`/images/${activity.type || 'no_image'}.svg`}
                      alt={activity.type || 'No Image'}
                      onError={(e) => {e.currentTarget.src = '/images/no_image.svg'}}
                    />
                    <span>{activity.type && activity.type !== 'null' ? activity.type.charAt(0).toUpperCase() + activity.type.slice(1) : ''}</span>
                  </div>
                  <h3>{epochConverter(activity.date, 'dd-mm-yyyy')}</h3>
                </div>
              )}
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
    </>
  )
}
