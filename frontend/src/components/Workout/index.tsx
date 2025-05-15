import * as React from 'react'
import { ReactNode } from 'react'
import { useNavigate, useParams } from 'react-router-dom'

import { Divisor } from '../Divisor'
import { Card } from '../Card'
import { ObjectList } from '../ObjectList'

import { epochConverter } from '../../utils/epochConverter'

import styles from './styles.module.css'
import { Activity } from '../Activity'

type WorkoutProps = {
  lastWorkoutContent: ReactNode
  workouts: any[] //GymActivities[] | WaterActivities[]
  onAdd: () => void
}

export function Workout({ lastWorkoutContent, workouts, onAdd }: WorkoutProps) {
  return (
    <Divisor
      left={
        <Card
          content={
            <div className={styles.lastWorkout}>
              <h1>Last workout</h1>
              {lastWorkoutContent}
            </div>
          }
          width="100%"
          height="80vh"
        />
      }
      right={
        <ObjectList
          items={workouts}
          getKey={workout => workout.id}
          onAdd={onAdd}
          renderItem={workout => <Activity activity={workout} />}
        />
      }
    />
  )
}
