import * as React from 'react'
import { useEffect, useReducer } from 'react'
import { useParams } from 'react-router-dom'

import { CircularProgress } from '@mui/material'
import { Divisor } from '../../components/Divisor'
import { Card } from '../../components/Card'
import { ManeuversCarrousel } from '../../components/ManeuversCarrousel'

import { WaterWorkout } from '../../types/WaterWorkout'

import { getWaterActivity, getQuestionnaire } from '../../services/waterServices'

import { epochConverter } from '../../utils/epochConverter'

import styles from './styles.module.css'

type State = {
  workout: WaterWorkout
  questionnaire: any
}

type Action = { type: 'setWorkout'; workout: WaterWorkout } | { type: 'setQuestionnaire'; payload: any }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'setWorkout':
      return { ...state, workout: action.workout }
    case 'setQuestionnaire':
      return { ...state, questionnaire: action.payload }
    default:
      return state
  }
}

export function WaterWorkoutsDetails() {
  const initialState: State = { workout: undefined, questionnaire: undefined }
  const [state, dispatch] = useReducer(reducer, initialState)
  const wid = useParams().wid

  useEffect(() => {
    async function fetchActivityData() {
      try {
        const res = await getWaterActivity(wid)
        dispatch({ type: 'setWorkout', workout: res })
      } catch (error) {
        console.error('Error fetching workout details:', error)
      }
    }
    async function fetchQuestionnaireData() {
      try {
        const res = await getQuestionnaire(wid)
        dispatch({ type: 'setQuestionnaire', payload: res })
      } catch (error) {
        dispatch({ type: 'setQuestionnaire', payload: null })
      }
    }
    fetchActivityData()
    fetchQuestionnaireData()
  }, [])

  const workout = state.workout

  if (workout === undefined) return <CircularProgress />

  const durationInMinutes = Math.floor(workout.duration / 60)
  const numberOfWaves = workout.waves.length
  const maneuversAttempts = workout.waves.reduce((acc, wave) => acc + wave.maneuvers.length, 0)
  const numberOfWavesPerMinute = (numberOfWaves / durationInMinutes).toFixed(2)
  const questionnaire = state.questionnaire

  const maneuvers = workout.waves.flatMap(wave => wave.maneuvers)
  const leftSideManeuvers = maneuvers.filter(maneuver => !maneuver.rightSide)
  const rightSideManeuvers = maneuvers.filter(maneuver => maneuver.rightSide)

  return (
    <Divisor
      left={
        <>
          <Card
            content={
              <div className={styles.workoutDetails}>
                <h1>Workout Details</h1>
                <h2>{epochConverter(workout.date, 'dd-mm-yyyy')}</h2>
                <div className={styles.workoutDetailsRow}>
                  <h2>Internal</h2>
                  <p>
                    <strong>PSE:</strong> {workout.pse}
                  </p>
                  <p>
                    <strong>Session PSE:</strong> {workout.pse * durationInMinutes}
                  </p>
                </div>
                <div>
                  <h2>External</h2>
                  <p>
                    <strong>Duration:</strong> {durationInMinutes} min
                  </p>
                  <p>
                    <strong>Number of Waves:</strong> {numberOfWaves}
                  </p>
                  <p>
                    <strong>Maneuvers Attempts:</strong> {maneuversAttempts}
                  </p>
                  <p>
                    <strong>Waves per Minute:</strong> {numberOfWavesPerMinute}
                  </p>
                </div>
              </div>
            }
          />
          <Card
            content={
              <>
                <h1>Wellness Questionnaire</h1>
                {questionnaire === undefined ? (
                  <CircularProgress />
                ) : questionnaire === null ? (
                  <p>No questionnaire data available.</p>
                ) : (
                    <div className={styles.questionnaireDetails}>
                      {Object.entries(questionnaire).map(([key, value]) => {
                        const formattedKey = key.replace(/([A-Z])/g, ' $1')
                          .trim()
                          .toLowerCase()
                          .split(' ')
                          .map(word => word.charAt(0).toUpperCase() + word.slice(1))
                          .join(' ')

                        return <p><strong>{formattedKey}:</strong> {String(value)}</p>
                      }
                      )}
                    </div>
                )}
              </>
            }
          />
        </>
      }
      right={
        <>
          <Card
          content={
            <>
              <h1>Waves</h1>
              <div className={styles.wavesContainer}>
                {workout.waves.length === 0 ? <p>No waves for this workout</p> : workout.waves.map((wave, index) => (
                  <div key={wave.id} className={styles.waveDetails}>
                    <h2>Wave {index + 1}</h2>
                    <div className={styles.maneuversContainer}>
                      {wave.maneuvers.map(maneuver => (
                        <div key={maneuver.id} className={styles.maneuver}>
                          <img src={maneuver.url || `/images/no_image.svg`} alt="Maneuver" />
                          {maneuver.name} - {maneuver.rightSide ? '➡️' : '⬅️'} {maneuver.success ? '✅' : '❌'}
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            </>
            }
          />
            <div className={styles.maneuversCarrousel}>
            {['Left', 'Right'].map(side => {
              const maneuvers = side === 'Left' ? leftSideManeuvers : rightSideManeuvers
              return (
                <Card 
                  key={side}
                  content={
                  maneuvers.length === 0 
                    ? <h1>No {side} side maneuvers</h1>
                    : <>
                      <h1>{side} Side Maneuvers</h1>
                      <ManeuversCarrousel maneuvers={maneuvers} />
                    </>
                  }
                  width="100%"
                />
              )
            })}
            </div>
        </>
      }
    />
  )
}
