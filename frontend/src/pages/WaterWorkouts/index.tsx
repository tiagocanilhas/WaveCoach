import * as React from 'react'
import { useParams } from 'react-router-dom'
import { useEffect, useReducer } from 'react'

import { CircularProgress } from '@mui/material'
import { Workout } from '../../components/Workout'
import { AddWaterWorkoutPopup } from '../../components/AddWaterWorkoutPopup'

import { getCalendar } from '../../services/athleteServices'

import styles from './styles.module.css'

import { Bar } from 'react-chartjs-2'
import { Chart as ChartJS, BarElement, CategoryScale, LinearScale, Tooltip, Legend } from 'chart.js'
import { Card } from '../../components/Card'
import { useAuthentication } from '../../hooks/useAuthentication'

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend)

const dummyData = {
  labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri'],
  datasets: [
    {
      label: 'Workouts',
      data: [1, 2, 0, 3, 1],
      backgroundColor: 'rgba(54, 162, 235, 0.6)',
      borderRadius: 4,
    },
  ],
}

type State = {
  activities: any[]
  isOpen: boolean
  workout: any
}

type Action =
  | { type: 'setActivities'; payload: any[] }
  | { type: 'setLastWorkout'; payload: any }
  | { type: 'addWorkout' }
  | { type: 'closeWorkout' }

function reducer(state: State, action: Action) {
  switch (action.type) {
    case 'setActivities':
      return { ...state, activities: action.payload }
    case 'setLastWorkout':
      return { ...state, workout: action.payload }
    case 'addWorkout':
      return { ...state, isOpen: true }
    case 'closeWorkout':
      return { ...state, isOpen: false }
    default:
      return state
  }
}

export function WaterWorkouts() {
  const initialState: State = { activities: undefined, isOpen: false, workout: undefined }
  const [state, dispatch] = useReducer(reducer, initialState)
  const id = useParams().aid
  const [user] = useAuthentication()

  useEffect(() => {
    async function fetchCalendar() {
      try {
        const res = await getCalendar(id, 'water')
        const activities = res.mesocycles.flatMap(mesocycle => mesocycle.microcycles).flatMap(microcycle => microcycle.activities)
        dispatch({ type: 'setActivities', payload: activities })
      } catch (error) {
        console.error('Error fetching data:', error)
      }
    }
    fetchCalendar()
  }, [])

  function handleAdd() {
    dispatch({ type: 'addWorkout' })
  }

  function handleClose() {
    dispatch({ type: 'closeWorkout' })
  }

  function handleOnSuccess() {
    window.location.reload()
  }

  if (state.activities === undefined) return <CircularProgress />

  return (
    <>
      <Workout
        lastWorkoutContent={<>{state.workout === undefined ? <CircularProgress /> : state.workout}</>}
        workouts={state.activities}
        onAdd={user.isCoach ? handleAdd : undefined}
      />

      <div className={styles.graphics}>
        <Card content={<Bar data={dummyData} />} width="500px" height="300px" />
        <Card content={<Bar data={dummyData} />} width="500px" height="300px" />
        <Card content={<Bar data={dummyData} />} width="500px" height="300px" />
        <Card content={<Bar data={dummyData} />} width="500px" height="300px" />
        <Card content={<Bar data={dummyData} />} width="500px" height="300px" />
        <Card content={<Bar data={dummyData} />} width="500px" height="300px" />
        <Card content={<Bar data={dummyData} />} width="500px" height="300px" />
      </div>

      {state.isOpen && <AddWaterWorkoutPopup onClose={handleClose} onSuccess={handleOnSuccess} />}
    </>
  )
}
