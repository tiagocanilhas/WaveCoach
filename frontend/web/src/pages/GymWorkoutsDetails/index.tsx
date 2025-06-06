import * as React from 'react'
import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'

import { CircularProgress } from '@mui/material'
import { Card } from '../../components/Card'

import { getGymActivity } from '../../../../services/gymServices'

import { epochConverter } from '../../../../utils/epochConverter'

import { Exercise } from '../../types/Exercise'

import styles from './styles.module.css'
import { GymWorkout } from '../../types/GymWorkout'

export function GymWorkoutsDetails() {
  const [workout, setWorkout] = useState<GymWorkout>(undefined)
  const gid = useParams().gid

  useEffect(() => {
    async function fetchData() {
      try {
        const res = await getGymActivity(gid)
        setWorkout(res)
      } catch (error) {
        console.error('Error fetching workout details:', error)
      }
    }
    fetchData()
  }, [])

  if (!workout) return <CircularProgress />

  return (
    <div className={styles.container}>
      <h2>{epochConverter(workout.date, 'dd-mm-yyyy')}</h2>
      {workout.exercises.map(exercise => (
        <Card
          content={
            <div className={styles.exercise}>
              <div className={styles.exerciseInfo}>
                <img src={exercise.url || `/images/no_image.svg`} alt="Exercise" />
                <h3>{exercise.name}</h3>
              </div>
              <ul>
                {exercise.sets.map((set, idx) => (
                  <li>
                    Set {idx + 1}: {set.reps} x {set.weight} kg - {set.restTime} sec'
                  </li>
                ))}
              </ul>
            </div>
          }
          width="600px"
        />
      ))}
    </div>
  )
}
