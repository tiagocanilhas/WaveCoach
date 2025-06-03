import * as React from 'react'
import { useParams } from 'react-router-dom'
import { useEffect, useReducer } from 'react'

import { CircularProgress } from '@mui/material'
import { Workout } from '../../components/Workout'
import { AddWaterWorkoutPopup } from '../../components/AddWaterWorkoutPopup'
import { WaterCharts } from '../../components/WaterCharts'

import { getWaterActivity } from '../../../services/waterServices'
import { getWaterActivities } from '../../../services/athleteServices'

import { handleError } from '../../../utils/handleError'

import { useAuthentication } from '../../hooks/useAuthentication'

import { WaterCalendar } from '../../types/WaterCalendar'

import styles from './styles.module.css'
import { epochConverter } from '../../../utils/epochConverter'

type State = {
  calendar: WaterCalendar
  isOpen: boolean
  workout: any
  selectedCycle?: { mesocycleId: number; microcycleId: number | null }
  error?: string
}

type Action =
  | { type: 'setCalendar'; payload: WaterCalendar }
  | { type: 'setLastWorkout'; payload: any }
  | { type: 'addWorkout' }
  | { type: 'closeWorkout' }
  | { type: 'setSelectedCycle'; payload: { mesocycleId: number; microcycleId: number | null } | null }
  | { type: 'error'; error: string }

function reducer(state: State, action: Action) {
  switch (action.type) {
    case 'setCalendar':
      return { ...state, calendar: action.payload }
    case 'setLastWorkout':
      return { ...state, workout: action.payload }
    case 'addWorkout':
      return { ...state, isOpen: true }
    case 'closeWorkout':
      return { ...state, isOpen: false }
    case 'setSelectedCycle':
      return { ...state, selectedCycle: action.payload }
    case 'error':
      return { ...state, error: action.error }
    default:
      return state
  }
}

export function WaterWorkouts() {
  const initialState: State = { calendar: undefined, isOpen: false, workout: undefined }
  const [state, dispatch] = useReducer(reducer, initialState)
  const id = useParams().aid
  const [user] = useAuthentication()

  async function fetchCalendar() {
    try {
      const calendar = await getWaterActivities(id)
      dispatch({ type: 'setCalendar', payload: calendar })

      const activities = calendar.mesocycles
        .flatMap(mesocycle => mesocycle.microcycles)
        .flatMap(microcycle => microcycle.activities)

      if (activities.length === 0) {
        dispatch({ type: 'setLastWorkout', payload: null })
        return
      }

      const wid = activities[activities.length - 1].id
      const workout = await getWaterActivity(wid)
      dispatch({ type: 'setLastWorkout', payload: workout })
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error) })
    }
  }

  useEffect(() => {
    fetchCalendar()
  }, [])

  function handleAdd() {
    dispatch({ type: 'addWorkout' })
  }

  function handleClose() {
    dispatch({ type: 'closeWorkout' })
  }

  function handleOnCycleSelected(cycle: { mesocycleId: number; microcycleId: number | null } | null) {
    dispatch({ type: 'setSelectedCycle', payload: cycle })
  }

  function handleOnSuccess() {
    fetchCalendar()
    dispatch({ type: 'closeWorkout' })
  }

  const workout = state.workout

  if (state.calendar === undefined) return <CircularProgress />

  return (
    <>
      <Workout
        lastWorkoutContent={
          <>
            {workout === undefined ? (
              <CircularProgress />
            ) : workout === null || workout.waves.length === 0 ? (
              <p>Data not available</p>
            ) : (
              <div className={styles.lastWorkout}>
                <h2>{epochConverter(workout.date, 'dd-mm-yyyy')}</h2>
                <div className={styles.lastWorkoutDetails}>
                  <p>PSE: {workout.pse} (0-10)</p>
                  <p>Condition: {workout.condition}</p>
                  <p>Heart Rate: {workout.heartRate} bpm</p>
                  <div>Duration: {workout.duration / 60} min</div>
                </div>
                {workout.waves.map((wave, index) => (
                  <div className={styles.wave} key={wave.id}>
                    <h2>{`Wave ${index + 1}`}</h2>
                    <div className={styles.maneuvers}>
                      {wave.maneuvers.map(maneuver => (
                        <div key={maneuver.id} className={styles.maneuver}>
                          <img src={maneuver.url || `/images/no_image.svg`} alt="Maneuver" />
                          <div className={styles.maneuverDetails}>
                            <p>{maneuver.name}</p>
                            <p>{maneuver.rightSide ? '➡️' : '⬅️'}</p>
                            <p>{maneuver.success ? '✅' : '❌'}</p>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </>
        }
        calendar={state.calendar}
        onAdd={user.isCoach ? handleAdd : undefined}
        onCycleSelected={handleOnCycleSelected}
        type="water"
      />

      <WaterCharts data={state.calendar} selected={state.selectedCycle} />

      {state.isOpen && <AddWaterWorkoutPopup onClose={handleClose} onSuccess={handleOnSuccess} />}
    </>
  )
}
