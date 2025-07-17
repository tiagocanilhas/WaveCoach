import * as React from 'react'
import { useReducer, useState } from 'react'

import { TextField } from '@mui/material'
import { EditWavePopup } from '../EditWavePopup'

import { WaterWorkoutWave } from '../../types/WaterWorkoutWave'

import styles from './styles.module.css'

type State = {
  points: number
}

type Action = { type: 'edit'; name: string; value: string | number }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'edit':
      return { ...state, [action.name]: action.value }
    default:
      return state
  }
}

type HandleHeatWaveProps = {
  wave?: WaterWorkoutWave
  setPoints: (points: number) => void
}

export function HandleHeatWave({ wave: initialWave, setPoints }: HandleHeatWaveProps) {
  const initialState: State = {
    points: initialWave ? initialWave.points : 0,
  }
  const [state, dispatch] = useReducer(reducer, initialState)

  function handleOnChange(event: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = event.target
    dispatch({ type: 'edit', name, value })
    if (name === 'points') {
      setPoints(Number(value))
    }
  }

  const points = state.points
  const rightSide = initialWave ? initialWave.rightSide : false
  const maneuvers = initialWave ? initialWave.maneuvers : []

  return (
    <div className={styles.container}>
      <TextField
        name="points"
        type="number"
        label="Points"
        value={points}
        onChange={handleOnChange}
        onClick={e => e.stopPropagation()}
      />
      <p>{rightSide ? '➡️' : '⬅️'}</p>
      <p>
        {maneuvers.map(m => (
          <p>
            {m.name} {m.success ? '✅' : '❌'}
          </p>
        ))}
      </p>
    </div>
  )
}
