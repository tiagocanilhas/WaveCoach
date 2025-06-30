import * as React from 'react'
import { ReactNode, useState } from 'react'

import { Divisor } from '../Divisor'
import { Card } from '../Card'
import { ObjectList } from '../ObjectList'

import styles from './styles.module.css'
import { Activity } from '../Activity'
import { CyclesSelect } from '../CyclesSelect'
import { WaterWorkout } from '../../types/WaterWorkout'
import { GymWorkout } from '../../types/GymWorkout'

type WorkoutProps = {
  lastWorkoutContent: ReactNode
  calendar: any
  onCycleSelected: (cycle: { mesocycleId: number; microcycleId: number | null } | null) => void
  type: 'water' | 'gym'
  onAdd: () => void
  onDeleteSuccess: () => void
}

export function Workout({ lastWorkoutContent, calendar, onCycleSelected, type, onAdd, onDeleteSuccess }: WorkoutProps) {
  const [cycleSelected, setCycleSelected] = useState<{ mesocycleId: number; microcycleId: number | null } | null>(null)

  function handleCycleSelect(cycle: { mesocycleId: number; microcycleId: number | null } | null) {
    setCycleSelected(cycle)
    onCycleSelected(cycle)
  }

  function getWorkouts() {
    const mesocycles = calendar.mesocycles

    if (!cycleSelected) return mesocycles.flatMap(m => m.microcycles).flatMap(mc => mc.activities)

    const { mesocycleId, microcycleId } = cycleSelected

    if (microcycleId !== null) {
      const mesocycle = mesocycles.find(m => m.id === mesocycleId)
      const microcycle = mesocycle?.microcycles.find(mc => mc.id === microcycleId)
      return microcycle?.activities ?? []
    } else {
      const mesocycle = mesocycles.find(m => m.id === mesocycleId)
      return mesocycle?.microcycles.flatMap(mc => mc.activities) ?? []
    }
  }

  const workouts = getWorkouts()

  return (
    <Divisor
      left={
        <Card
          content={
            <div className={styles.lastWorkout}>
              <h1>Last workout</h1>
              <div className={styles.lastWorkoutContent}>{lastWorkoutContent}</div>
            </div>
          }
          width="100%"
          height="80vh"
        />
      }
      right={
        <>
          {calendar.mesocycles.length > 0 && (
            <Card
              content={<CyclesSelect cycles={calendar.mesocycles} cycleSelected={cycleSelected} onSelect={handleCycleSelect} />}
              width="100%"
            />
          )}
          <ObjectList<WaterWorkout | GymWorkout>
            items={workouts}
            getKey={workout => workout.id}
            onAdd={onAdd}
            renderItem={workout => (
              <Activity activity={{ id: workout.id, type: type, date: workout.date }} onDeleteSuccess={onDeleteSuccess} />
            )}
          />
        </>
      }
    />
  )
}
