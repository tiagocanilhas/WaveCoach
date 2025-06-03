import * as React from 'react'
import { useReducer } from 'react'
import { useParams } from 'react-router-dom'

import { Slider, TextField } from '@mui/material'
import { Card } from '../Card'
import { Popup } from '../Popup'
import { Button } from '../Button'
import { AddWavePopup } from '../AddWavePopup'
import { CustomTimePicker } from '../CustomTimePicker'

// import { Wave } from '../../types/Wave'
import { WaveToAdd } from '../../types/WaveToAdd'

import { createWaterActivity } from '../../../services/waterServices'

import { handleError } from '../../../utils/handleError'

import styles from './styles.module.css'

type State = {
  isOpen: boolean
  date: string
  condition: string
  pse: number
  time: number
  heartRate: number
  waves: WaveToAdd[]
  error?: string
}

type Action =
  | { type: 'openPopup' }
  | { type: 'closePopup' }
  | { type: 'setDate'; date: string }
  | { type: 'setCondition'; condition: string }
  | { type: 'setPse'; pse: number }
  | { type: 'setTime'; time: number }
  | { type: 'setHeartRate'; heartRate: number }
  | { type: 'addWave'; wave: WaveToAdd }
  | { type: 'error'; error: string }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'openPopup':
      return { ...state, isOpen: true }
    case 'closePopup':
      return { ...state, isOpen: false }
    case 'setDate':
      return { ...state, date: action.date }
    case 'setCondition':
      return { ...state, condition: action.condition }
    case 'setPse':
      return { ...state, pse: action.pse }
    case 'setTime':
      return { ...state, time: action.time }
    case 'setHeartRate':
      return { ...state, heartRate: action.heartRate }
    case 'addWave':
      return {
        ...state,
        isOpen: false,
        waves: [...state.waves, action.wave],
      }
    case 'error':
      return { ...state, error: action.error }
    default:
      return state
  }
}

type AddWaterWorkoutPopupProps = {
  onClose: () => void
  onSuccess: () => void
}

const initialState: State = {
  date: new Date().toISOString().split('T')[0],
  isOpen: false,
  condition: '',
  pse: 0,
  time: 0,
  heartRate: 0,
  waves: [],
}

export function AddWaterWorkoutPopup({ onClose, onSuccess }: AddWaterWorkoutPopupProps) {
  const [state, dispatch] = useReducer(reducer, initialState)
  const id = useParams().aid

  function handleAddExercise() {
    dispatch({ type: 'openPopup' })
  }

  function handleCloseExercise() {
    dispatch({ type: 'closePopup' })
  }

  function onAddWave(wave: WaveToAdd) {
    dispatch({ type: 'addWave', wave })
  }

  function handleOnChange(event: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = event.target
    switch (name) {
      case 'date':
        dispatch({ type: 'setDate', date: value })
        break
      case 'condition':
        dispatch({ type: 'setCondition', condition: value })
        break
      default:
        break
    }
  }

  function handleOnChangeSlider(event: Event, newValue: number) {
    const target = event.target as HTMLInputElement
    const name = target.name
    switch (name) {
      case 'pse':
        dispatch({ type: 'setPse', pse: newValue })
        break
      case 'heartRate':
        dispatch({ type: 'setHeartRate', heartRate: newValue })
        break
      default:
        break
    }
  }

  function handleOnChangeTime(value: number) {
    dispatch({ type: 'setTime', time: value })
  }

  async function handleOnSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()

    const date = state.date
    const condition = state.condition
    const pse = state.pse
    const time = state.time
    const heartRate = state.heartRate
    const waves = state.waves.map(wave => ({
      maneuvers: wave.maneuvers.map(m => ({
        waterManeuverId: m.waterManeuverId,
        rightSide: m.rightSide,
        success: m.success,
      })),
    }))

    try {
      await createWaterActivity(id, date, pse, condition, heartRate, time, waves)
      onSuccess()
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error) })
    }
  }

  const isOpen = state.isOpen
  const date = state.date
  const condition = state.condition
  const pse = state.pse
  const time = state.time
  const heartRate = state.heartRate
  const waves = state.waves
  const disabled = state.waves.length === 0 || date.length === 0

  return (
    <>
      <Popup
        title="Add Workout"
        content={
          <>
            <form className={styles.addWorkout} onSubmit={handleOnSubmit}>
              <TextField type="date" name="date" value={date} onChange={handleOnChange} />
              <TextField type="text" name="condition" label="Condition" value={condition} onChange={handleOnChange} />
              <div className={styles.sliderContainer}>
                <label>PSE</label>
                <Slider
                  name="pse"
                  value={pse}
                  onChange={handleOnChangeSlider}
                  min={0}
                  max={10}
                  step={1}
                  marks
                  valueLabelDisplay="auto"
                />
              </div>
              <CustomTimePicker onChange={handleOnChangeTime} defaultValue={time} />
              <div className={styles.sliderContainer}>
                <label>Heart Rate</label>
                <Slider
                  name="heartRate"
                  value={heartRate}
                  min={50}
                  max={190}
                  onChange={handleOnChangeSlider}
                  step={1}
                  defaultValue={70}
                  valueLabelDisplay="auto"
                />
              </div>
              <div className={styles.wavesContainer}>
                {waves.map(info => (
                  <Card
                    content={
                      <div className={styles.exercise}>
                        {info.maneuvers.map(m => {
                          return (
                            <p>
                              {m.name} - {m.rightSide ? '➡️' : '⬅️'} {m.success ? '✅' : '❌'}
                            </p>
                          )
                        })}
                      </div>
                    }
                    width="600px"
                  />
                ))}
                <Card
                  content={
                    <div className={styles.add} onClick={handleAddExercise}>
                      +
                    </div>
                  }
                  width="600px"
                />
              </div>
              <Button text="Add" type="submit" disabled={disabled} width="100%" height="30px" />
            </form>
            {state.error && <p className={styles.error}>{state.error}</p>}
          </>
        }
        onClose={onClose}
      />

      {isOpen && <AddWavePopup onAdd={onAddWave} onClose={handleCloseExercise} />}
    </>
  )
}
