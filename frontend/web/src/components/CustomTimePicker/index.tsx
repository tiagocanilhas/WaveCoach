import React, { useEffect, useReducer, useState } from 'react'
import { Select, MenuItem, InputLabel } from '@mui/material'

import styles from './styles.module.css'

type State = {
  hours: number
  minutes: number
  seconds: number
}

type Action =
  | { type: 'setHours'; payload: number }
  | { type: 'setMinutes'; payload: number }
  | { type: 'setSeconds'; payload: number }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'setHours':
      return { ...state, hours: action.payload }
    case 'setMinutes':
      return { ...state, minutes: action.payload }
    case 'setSeconds':
      return { ...state, seconds: action.payload }
    default:
      return state
  }
}

type CustomTimePickerProps = {
  onChange: (value: number) => void
  defaultValue?: number
}

export function CustomTimePicker({ onChange, defaultValue = 0 }: CustomTimePickerProps) {
  const initialState: State = {
    hours: Math.floor(defaultValue / 3600),
    minutes: Math.floor((defaultValue % 3600) / 60),
    seconds: defaultValue % 60,
  }

  const [state, dispatch] = useReducer(reducer, initialState)

  useEffect(() => {
    const totalSeconds = state.hours * 3600 + state.minutes * 60 + state.seconds
    onChange(totalSeconds)
  }, [state])

  function setNewValue(type: string, value: number) {
    switch (type) {
      case 'hours':
        dispatch({ type: 'setHours', payload: value })
        break
      case 'minutes':
        dispatch({ type: 'setMinutes', payload: value })
        break
      case 'seconds':
        dispatch({ type: 'setSeconds', payload: value })
        break
      default:
        break
    }
  }

  const hours = state.hours
  const minutes = state.minutes
  const seconds = state.seconds

  return (
    <div className={styles.container}>
      <div className={styles.timePicker}>
        <InputLabel>Hours</InputLabel>
        <Select value={hours} onChange={e => setNewValue('hours', Number(e.target.value))}>
          {Array.from({ length: 24 }, (_, i) => (
            <MenuItem key={i} value={i}>
              {i.toString().padStart(2, '0')}
            </MenuItem>
          ))}
        </Select>
      </div>

      <div className={styles.timePicker}>
        <InputLabel>Minutes</InputLabel>
        <Select value={minutes} onChange={e => setNewValue('minutes', Number(e.target.value))}>
          {Array.from({ length: 60 }, (_, i) => (
            <MenuItem key={i} value={i}>
              {i.toString().padStart(2, '0')}
            </MenuItem>
          ))}
        </Select>
      </div>

      <div className={styles.timePicker}>
        <InputLabel>Seconds</InputLabel>
        <Select value={seconds} onChange={e => setNewValue('seconds', Number(e.target.value))}>
          {Array.from({ length: 60 }, (_, i) => (
            <MenuItem key={i} value={i}>
              {i.toString().padStart(2, '0')}
            </MenuItem>
          ))}
        </Select>
      </div>
    </div>
  )
}
