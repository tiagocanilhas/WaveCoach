import * as React from 'react'
import { useEffect, useReducer } from 'react'
import { Navigate, useParams } from 'react-router-dom'

import { CircularProgress } from '@mui/material'
import { Divisor } from '../../components/Divisor'
import { Card } from '../../components/Card'
import { ManeuversCarrousel } from '../../components/ManeuversCarrousel'
import { AddQuestionnairePopup } from '../../components/AddQuestionnairePopup'
import { Button } from '../../components/Button'
import { EditWaterWorkoutPopup } from '../../components/EditWaterWorkoutPopup'

import { WaterWorkout } from '../../types/WaterWorkout'
import { Questionnaire } from '../../types/Questionnaire'

import { getWaterActivity, getQuestionnaire } from '../../../../services/waterServices'

import { epochConverter } from '../../../../utils/epochConverter'

import styles from './styles.module.css'

type State =
  | { tag: 'loading'; workout?: WaterWorkout; questionnaire?: Questionnaire }
  | { tag: 'loaded'; workout: WaterWorkout; questionnaire: Questionnaire; isEditing: Boolean; isQuestionnairePopupOpen?: boolean }
  | { tag: 'error' }

type Action =
  | { type: 'setWorkout'; workout: WaterWorkout }
  | { type: 'toggleEditing' }
  | { type: 'setError' }
  | { type: 'setQuestionnaire'; questionnaire: Questionnaire }
  | { type: 'toggleQuestionnairePopup' }

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'loading':
      switch (action.type) {
        case 'setWorkout':
          if (state.questionnaire !== undefined) {
            return { tag: 'loaded', workout: action.workout, questionnaire: state.questionnaire, isEditing: false }
          }
          return { tag: 'loading', workout: action.workout, questionnaire: state.questionnaire }
        case 'setQuestionnaire':
          if (state.workout !== undefined) {
            return { tag: 'loaded', workout: state.workout, questionnaire: action.questionnaire, isEditing: false }
          }
          return { tag: 'loading', workout: state.workout, questionnaire: action.questionnaire }
        case 'setError':
          return { tag: 'error' }
        default:
          return state
      }
    case 'loaded':
      switch (action.type) {
        case 'toggleEditing':
          return { ...state, isEditing: !state.isEditing }
        case 'setWorkout':
          return { ...state, workout: action.workout }
        case 'setQuestionnaire':
          return { ...state, questionnaire: action.questionnaire }
        case 'toggleQuestionnairePopup':
          return { ...state, isQuestionnairePopupOpen: !state.isQuestionnairePopupOpen }
        default:
          return state
      }
    case 'error':
      return state
  }
}

export function WaterWorkoutsDetails() {
  const initialState: State = { tag: 'loading' }
  const [state, dispatch] = useReducer(reducer, initialState)
  const wid = useParams().wid

  async function fetchActivityData() {
    try {
      const { status, res } = await getWaterActivity(wid)
      dispatch({ type: 'setWorkout', workout: res })
    } catch (error) {
      dispatch({ type: 'setError' })
    }
  }

  async function fetchQuestionnaireData() {
    try {
      const { status, res } = await getQuestionnaire(wid)
      dispatch({ type: 'setQuestionnaire', questionnaire: res })
    } catch (error) {
      dispatch({ type: 'setQuestionnaire', questionnaire: null })
    }
  }

  useEffect(() => {
    fetchActivityData()
    fetchQuestionnaireData()
  }, [])

  function handleEditWorkout() {
    dispatch({ type: 'toggleEditing' })
  }

  function handleOnCloseEditing() {
    dispatch({ type: 'toggleEditing' })
  }

  async function handleOnSuccess() {
    await fetchActivityData()
    dispatch({ type: 'toggleEditing' })
  }

  function handleAddQuestionnaire() {
    dispatch({ type: 'toggleQuestionnairePopup' })
  }

  async function handleQuestionnaireSuccess() {
    await fetchQuestionnaireData()
    dispatch({ type: 'toggleQuestionnairePopup' })
  }

  const workout = state.tag === 'loaded' ? state.workout : null

  if (state.tag === 'loading') return <CircularProgress />

  if (state.tag === 'error') return <Navigate to="/error" replace />

  const durationInMinutes = Math.floor(workout.duration / 60)
  const numberOfWaves = workout.waves.length
  const maneuversAttempts = workout.waves.reduce((acc, wave) => acc + wave.maneuvers.length, 0)
  const numberOfWavesPerMinute = (numberOfWaves / durationInMinutes).toFixed(2)
  const questionnaire = state.tag === 'loaded' ? state.questionnaire : null

  const leftSideManeuvers = workout.waves.filter(wave => wave.rightSide).flatMap(wave => wave.maneuvers)
  const rightSideManeuvers = workout.waves.filter(wave => !wave.rightSide).flatMap(wave => wave.maneuvers)

  return (
    <>
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
                      <strong>RPE:</strong> {workout.rpe} (0-10)
                    </p>
                    <p>
                      <strong>Session RPE:</strong> {workout.rpe * durationInMinutes}
                    </p>
                    <p>
                      <strong>Condition:</strong> {workout.condition}
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
                  <div className={styles.questionnaireHeader}>
                    <h1>Wellness Questionnaire</h1>
                    {questionnaire === null && (
                      <Card
                        content={
                          <div className={styles.add} onClick={handleAddQuestionnaire}>
                            +
                          </div>
                        }
                      />
                    )}
                  </div>
                  {questionnaire === undefined ? (
                    <CircularProgress />
                  ) : (
                    questionnaire !== null && (
                      <div className={styles.questionnaireDetails}>
                        {Object.entries(questionnaire).map(([key, value]) => {
                          const formattedKey = key
                            .replace(/([A-Z])/g, ' $1')
                            .trim()
                            .toLowerCase()
                            .split(' ')
                            .map(word => word.charAt(0).toUpperCase() + word.slice(1))
                            .join(' ')

                          return (
                            <p>
                              <strong>{formattedKey}:</strong> {String(value)}
                            </p>
                          )
                        })}
                      </div>
                    )
                  )}
                </>
              }
            />
            <Button text="Edit Workout" onClick={handleEditWorkout} height="50px" />
          </>
        }
        right={
          <>
            <Card
              content={
                <>
                  <h1>Waves</h1>
                  <div className={styles.wavesContainer}>
                    {workout.waves.length === 0 ? (
                      <p>No waves for this workout</p>
                    ) : (
                      workout.waves.map((wave, index) => (
                        <div key={wave.id} className={styles.waveDetails}>
                          <h2>
                            Wave {index + 1} {wave.rightSide ? '➡️' : '⬅️'}
                          </h2>
                          <div className={styles.maneuversContainer}>
                            {wave.maneuvers.map(maneuver => (
                              <div key={maneuver.id} className={styles.maneuver}>
                                <img src={maneuver.url || `/images/no_image.svg`} alt="Maneuver" />
                                {maneuver.name} - {maneuver.success ? '✅' : '❌'}
                              </div>
                            ))}
                          </div>
                        </div>
                      ))
                    )}
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
                      maneuvers.length === 0 ? (
                        <h1>No {side} side maneuvers</h1>
                      ) : (
                        <>
                          <h1>{side} Side Maneuvers</h1>
                          <ManeuversCarrousel maneuvers={maneuvers} />
                        </>
                      )
                    }
                    width="100%"
                  />
                )
              })}
            </div>
          </>
        }
      />

      {state.isEditing && (
        <EditWaterWorkoutPopup workout={state.workout} onClose={handleOnCloseEditing} onSuccess={handleOnSuccess} />
      )}

      {state.isQuestionnairePopupOpen && (
        <AddQuestionnairePopup onClose={handleAddQuestionnaire} onSuccess={handleQuestionnaireSuccess} />
      )}
    </>
  )
}
