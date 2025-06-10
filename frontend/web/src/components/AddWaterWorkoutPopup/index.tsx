import * as React from 'react'
import { useReducer } from 'react'
import { useParams } from 'react-router-dom'

import { Slider, TextField } from '@mui/material'
import { Popup } from '../Popup'
import { Button } from '../Button'
import { AddWavePopup } from '../AddWavePopup'
import { CustomTimePicker } from '../CustomTimePicker'

import { WaveToAdd } from '../../types/WaveToAdd'

import { createWaterActivity } from '../../../../services/waterServices'

import { handleError } from '../../../../utils/handleError'

import styles from './styles.module.css'
import { ManeuverToAdd } from '../../types/ManeuverToAdd'
import { VerticalReorderableList } from '../VerticalReorderableList'

type State = {
  isAdding: boolean
  date: string
  condition: string
  rpe: number
  time: number
  trimp: number
  waveToEdit: WaveToAdd | null
  waves: WaveToAdd[]
  error?: string
}

type Action =
  | { type: 'toggleIsAdding' }
  | { type: 'setDate'; date: string }
  | { type: 'setCondition'; condition: string }
  | { type: 'setRpe'; rpe: number }
  | { type: 'setTime'; time: number }
  | { type: 'setTrimp'; trimp: number }
  | { type: 'addWave'; wave: WaveToAdd }
  | { type: 'setWaveToEdit'; wave: WaveToAdd | null }
  | { type: 'updateWave'; wave: WaveToAdd }
  | { type: 'deleteWave'; tempId: string }
  | { type: 'setWaves'; waves: WaveToAdd[] }
  | { type: 'error'; error: string }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'toggleIsAdding':
      return { ...state, isAdding: !state.isAdding }
    case 'setDate':
      return { ...state, date: action.date }
    case 'setCondition':
      return { ...state, condition: action.condition }
    case 'setRpe':
      return { ...state, rpe: action.rpe }
    case 'setTime':
      return { ...state, time: action.time }
    case 'setTrimp':
      return { ...state, trimp: action.trimp }
    case 'addWave':
      return {
        ...state,
        isAdding: false,
        waves: [...state.waves, action.wave],
      }
    case 'setWaveToEdit':
      return { ...state, waveToEdit: action.wave }
    case 'updateWave':
      return {
        ...state,
        waves: state.waves.map(wave =>
          wave.tempId === action.wave.tempId ? action.wave : wave
        ),
        waveToEdit: null,
      }
    case 'deleteWave':
      return { ...state, waves: state.waves.filter(wave => wave.tempId !== action.tempId) }
    case 'setWaves':
      return { ...state, waves: action.waves }
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
  isAdding: false,
  condition: '',
  rpe: 0,
  time: 0,
  trimp: 0,
  waveToEdit: null,
  waves: [],
}

export function AddWaterWorkoutPopup({ onClose, onSuccess }: AddWaterWorkoutPopupProps) {
  const [state, dispatch] = useReducer(reducer, initialState)
  const id = useParams().aid

  function handleToggleIsAdding() {
    dispatch({ type: 'toggleIsAdding' })
  }

  function onAddWave(maneuvers: ManeuverToAdd[], rightSide: boolean) {
    dispatch({ type: 'addWave', wave: { tempId: Date.now().toString(), maneuvers, rightSide } })
  }

  function handleSetWaveToEdit(wave: WaveToAdd) {
    dispatch({ type: 'setWaveToEdit', wave })
  }

  function handleUpdateWave(maneuvers: ManeuverToAdd[], rightSide: boolean) {
    dispatch({ type: 'updateWave', wave: { ...state.waveToEdit, maneuvers, rightSide } })
  }

  function handleDeleteWave(tempId: string) {
    dispatch({ type: 'deleteWave', tempId })
  }

  function handleSetWaves(waves: WaveToAdd[]) {
    dispatch({ type: 'setWaves', waves })
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
      case 'rpe':
        dispatch({ type: 'setRpe', rpe: newValue })
        break
      case 'trimp':
        dispatch({ type: 'setTrimp', trimp: newValue })
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
    const rpe = state.rpe
    const time = state.time
    const trimp = state.trimp
    const waves = state.waves.map(wave => ({
      rightSide: wave.rightSide,
      maneuvers: wave.maneuvers.map(m => ({
        waterManeuverId: m.maneuver.id,
        success: m.success,
      })),
    }))

    try {
      await createWaterActivity(id, date, rpe, condition, trimp, time, waves)
      onSuccess()
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error) })
    }
  }

  const isAdding = state.isAdding
  const date = state.date
  const condition = state.condition
  const rpe = state.rpe
  const time = state.time
  const trimp = state.trimp
  const waves = state.waves
  const waveToEdit = state.waveToEdit
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
                <label>RPE</label>
                <Slider
                  name="rpe"
                  value={rpe}
                  onChange={handleOnChangeSlider}
                  min={1}
                  max={10}
                  step={1}
                  marks
                  valueLabelDisplay="auto"
                />
              </div>
              <CustomTimePicker onChange={handleOnChangeTime} defaultValue={time} />
              <div className={styles.sliderContainer}>
                <label>TRIMP</label>
                <Slider
                  name="trimp"
                  value={trimp}
                  min={50}
                  max={190}
                  onChange={handleOnChangeSlider}
                  step={1}
                  defaultValue={70}
                  valueLabelDisplay="auto"
                />
              </div>
              <div className={styles.wavesContainer}>
                <VerticalReorderableList<WaveToAdd>
                  list={waves}
                  renderItem={info => (
                      <div>
                        <p>Side: {info.rightSide ? '➡️' : '⬅️'}</p>
                        {info.maneuvers.map(m => (
                            <p>
                              {m.maneuver.name} {m.success ? '✅' : '❌'}
                            </p>
                          )
                        )}
                      </div>
                  )}
                  onReorder={handleSetWaves}
                  onClick={item => handleSetWaveToEdit(item)}
                  onDelete={item => handleDeleteWave(item.tempId)}
                  onAdd={handleToggleIsAdding}
                />
              </div>
              <Button text="Add" type="submit" disabled={disabled} width="100%" height="30px" />
            </form>
            {state.error && <p className={styles.error}>{state.error}</p>}
          </>
        }
        onClose={onClose}
      />

      {(isAdding || waveToEdit) && <AddWavePopup 
        data={waveToEdit} 
        onAdd={waveToEdit ? handleUpdateWave : onAddWave}
        onClose={waveToEdit ? () => handleSetWaveToEdit(null) : handleToggleIsAdding}
        />}
    </>
  )
}
