import * as React from 'react'

import { Card } from '../../components/Card'

import { epochConverter } from '../../utils/epochConverter'

import styles from './styles.module.css'

export function GymWorkoutsDetails() {
  const workout = {
    id: 1,
    date: 1672531199000,
    exercises: [
      {
        name: 'Bench Press',
        sets: [
          {
            id: 1,
            reps: 10,
            weight: 60,
            rest: 2,
          },
          {
            id: 2,
            reps: 8,
            weight: 70,
            rest: 2,
          },
        ],
      },
      {
        name: 'Squat',
        sets: [
          {
            id: 3,
            reps: 10,
            weight: 80,
            rest: 2,
          },
          {
            id: 4,
            reps: 8,
            weight: 90,
            rest: 2,
          },
          {
            id: 5,
            reps: 8,
            weight: 90,
            rest: 2,
          },
        ],
      },
      {
        name: 'Deadlift',
        sets: [
          {
            id: 6,
            reps: 10,
            weight: 100,
            rest: 2,
          },
          {
            id: 7,
            reps: 8,
            weight: 110,
            rest: 2,
          },
        ],
      },
      {
        name: 'Shoulder Press',
        sets: [
          {
            id: 8,
            reps: 10,
            weight: 40,
            rest: 2,
          },
          {
            id: 9,
            reps: 8,
            weight: 50,
            rest: 2,
          },
        ],
      },
      {
        name: 'Pull Up',
        sets: [
          {
            id: 10,
            reps: 10,
            weight: 0,
            rest: 2,
          },
          {
            id: 11,
            reps: 8,
            weight: 0,
            rest: 2,
          },
        ],
      },
    ],
  }

  return (
    <div className={styles.container}>
      <h2>{epochConverter(workout.date, 'dd-mm-yyyy')}</h2>
      {workout.exercises.map(exercise => (
        <Card
          content={
            <div className={styles.exercise}>
              <div className={styles.exerciseInfo}>
                <img src={`/images/no_image.svg`} alt="Exercise" />
                <h3>{exercise.name}</h3>
              </div>
              <ul>
                {exercise.sets.map((set, idx) => (
                  <li>
                    Set {idx + 1}: {set.reps} x {set.weight} kg - {set.rest}'
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
