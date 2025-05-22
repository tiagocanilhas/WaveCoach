import * as React from 'react'
import { useEffect, useReducer } from 'react'
import { useParams } from 'react-router-dom'

import { Divisor } from '../../components/Divisor'
import { Card } from '../../components/Card'

import { WaterWorkout } from '../../types/WaterWorkout'

import { getWaterActivity } from '../../services/waterServices'

import styles from './styles.module.css'

type State = {
  workout: WaterWorkout
  questionnaire: any
}
type Action = { type: 'setWorkout'; payload: WaterWorkout } | { type: 'setQuestionnaire'; payload: any }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'setWorkout':
      return { ...state, workout: action.payload }
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
        // const res = await getWaterActivity(wid);
        // setWorkout(res);
      } catch (error) {
        console.error('Error fetching workout details:', error)
      }
    }
    async function fetchQuestionnaireData() {
      try {
        // const res = await getWaterActivity(wid);
        // setWorkout(res);
      } catch (error) {
        console.error('Error fetching questionnaire details:', error)
      }
    }
    fetchActivityData()
    fetchQuestionnaireData()
  }, [])

  const workout = state.workout
  const questionnaire = state.questionnaire

  return (
    <Divisor
      left={
        <>
          <Card
            content={
              <>
                <h1>Workout Details</h1>
                <h2>Internal</h2>
                <h2>External</h2>
              </>
            }
          />
          <Card
            content={
              <>
                <h1>Wellness Questionnaire</h1>
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
              </>
            }
          />
          <Card content={<></>} />
        </>
      }
    />
  )
}
