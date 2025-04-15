import * as React from 'react'
import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'

import { CircularProgress } from '@mui/material'
import { Dropdown } from '../../components/Dropdown'

import { Athlete } from '../../types/athlete'

import { getAthlete } from '../../services/athleteServices'

import styles from './styles.module.css'
import { Card } from '../../components/Card'

export function Athlete() {
  const [athlete, setAthlete] = useState<Athlete | undefined>(undefined)
  const [workouts, setWorkouts] = useState<any[]>([])
  const id = useParams().aid

  useEffect(() => {
    async function fetchAthlete() {
      try {
        const res = await getAthlete(id)
        setAthlete(res)
      } catch (error) {
        console.error('Error fetching athlete:', error)
      }
    }
    async function fetchWorkouts() {
      try {
        const res = {
          workouts: [
            { date: '2023-10-01', type: 'Running' },
            { date: '2023-10-02', type: 'Cycling' },
            { date: '2023-10-03', type: 'Swimming' },
            { date: '2023-10-04', type: 'Weightlifting' },
            { date: '2023-10-05', type: 'Yoga' },
          ],
        } //await getAthlete(id)
        setWorkouts(res.workouts)
      } catch (error) {
        console.error('Error fetching workouts:', error)
      }
    }

    fetchAthlete()
    fetchWorkouts()
  }, [id])

  function handleEdit() {
    console.log('Edit athlete')
  }

  function handleDelete() {
    console.log('Delete athlete')
  }

  if (athlete === undefined) {
    return <CircularProgress className={styles.waiting} />
  }

  return (
    <div className={styles.container}>
      <div className={styles.athleteCard}>
        <Card
          content={
            <div className={styles.athlete}>
              <Dropdown
                options={[
                  { label: 'Edit', onClick: handleEdit },
                  { label: 'Delete', onClick: handleDelete },
                ]}
              />
              <img src={/*athlete.img ||*/ '/images/anonymous-user.webp'} alt={athlete.name || 'Anonymous'} />
              <h2 className={styles.name}>{athlete.name}</h2>
              <p className={styles.age}>{convertEpochTimeToAge(athlete.birthDate)} Years</p>
            </div>
          }
        />
      </div>

      <div className={styles.calendar}>
        <div className={styles.calendarContent}>
          <h2>Calendar</h2>
          <div className={styles.workoutsContainer}>
            {workouts.map((workout, index) => (
              <Card
                content={
                  <>
                    <div key={index} className={styles.workoutCard}>
                      <h3>{workout.date}</h3>
                      <p>{workout.type}</p>
                    </div>
                  </>
                }
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

function convertEpochTimeToAge(epochTime: number): number {
  const birthDate = new Date(epochTime)
  const today = new Date()
  const age = today.getFullYear() - birthDate.getFullYear()
  const monthDiff = today.getMonth() - birthDate.getMonth()

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) return age - 1

  return age
}
