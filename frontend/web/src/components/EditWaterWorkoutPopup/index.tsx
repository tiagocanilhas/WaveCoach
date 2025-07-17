import * as React from 'react'
import { useReducer } from 'react'
import { useParams } from 'react-router-dom'

import { Slider, TextField } from '@mui/material'
import { Popup } from '../Popup'
import { Button } from '../Button'
import { AddWavePopup } from '../AddWavePopup'
import { EditWavePopup } from '../EditWavePopup'
import { CustomTimePicker } from '../CustomTimePicker'
import { ReorderableList } from '../ReorderableList'

import { WaterWorkout } from '../../types/WaterWorkout'
import { WaterWorkoutWave } from '../../types/WaterWorkoutWave'
import { Maneuver } from '../../types/Maneuver'

import { createWaterActivity, updateWaterActivity } from '../../../../services/waterServices'

import { handleError } from '../../../../utils/handleError'
import { WorkoutEditing } from '../../../../utils/WorkoutEditing'

import styles from './styles.module.css'
import { epochConverter } from '../../../../utils/epochConverter'
import { diffListOrNull } from '../../../../utils/diffListOrNull'

type State =
  | {
      tag: 'editing'
      isAdding: boolean
      date: string
      condition: string
      rpe: number
      duration: number
      trimp: number
      waveToEdit: WaterWorkoutWave | null
      waves: WaterWorkoutWave[]
      removedWaves: WaterWorkoutWave[]
      error?: string
    }
  | {
      tag: 'submitting'
      isAdding: boolean
      date: string
      condition: string
      rpe: number
      duration: number
      trimp: number
      waves: WaterWorkoutWave[]
      removedWaves: WaterWorkoutWave[]
      waveToEdit: WaterWorkoutWave | null
    }
  | { tag: 'submitted' }

type Action =
  | { type: 'toggleIsAdding' }
  | { type: 'setValue'; name: string; value: string | number }
  | { type: 'addWave'; wave: WaterWorkoutWave }
  | { type: 'setWaveToEdit'; wave: WaterWorkoutWave | null }
  | { type: 'updateWave'; wave: WaterWorkoutWave }
  | { type: 'deleteWave'; id: number; tempId?: number }
  | { type: 'setWaves'; waves: WaterWorkoutWave[] }
  | { type: 'error'; error: string }
  | { type: 'submit' }
  | { type: 'success' }

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'editing':
      switch (action.type) {
        case 'toggleIsAdding':
          return { ...state, isAdding: !state.isAdding }
        case 'setValue':
          return { ...state, [action.name]: action.value }
        case 'addWave':
          return {
            ...state,
            isAdding: false,
            waves: [...state.waves, action.wave],
          }
        case 'setWaveToEdit':
          return { ...state, waveToEdit: action.wave, isAdding: false }
        case 'updateWave':
          return {
            ...state,
            waves: state.waves.map(wave => {
              const matchById = wave.id !== null && wave.id === action.wave.id
              const matchByTempId = wave.id === null && wave.tempId === action.wave.tempId
              return matchById || matchByTempId ? action.wave : wave
            }),
            waveToEdit: null,
          }
        case 'deleteWave':
          const removedWave = state.waves.find(wave => (wave.id === null ? wave.tempId === action.tempId : wave.id === action.id))
          if (!removedWave) return state

          return {
            ...state,
            waves: state.waves.filter(wave => wave !== removedWave),
            removedWaves:
              removedWave.id !== null
                ? [...state.removedWaves, WorkoutEditing.nullifyFieldsExceptId(removedWave)]
                : state.removedWaves,
          }
        case 'setWaves':
          return { ...state, waves: action.waves }
        case 'error':
          return { ...state, error: action.error }
        case 'submit':
          return { ...state, tag: 'submitting' }
        default:
          return state
      }
    case 'submitting':
      switch (action.type) {
        case 'success':
          return { ...state, tag: 'submitted' }
        case 'error':
          return { ...state, tag: 'editing', error: action.error }
        default:
          return state
      }
    case 'submitted':
      return state
    default:
      return state
  }
}

type EditWaterWorkoutPopupProps = {
  workout: WaterWorkout
  onClose: () => void
  onSuccess: () => void
}

export function EditWaterWorkoutPopup({ workout, onClose, onSuccess }: EditWaterWorkoutPopupProps) {
  const initialState: State = {
    tag: 'editing',
    date: epochConverter(workout.date, 'yyyy-mm-dd'),
    isAdding: false,
    condition: workout.condition,
    rpe: workout.rpe,
    duration: workout.duration,
    trimp: workout.trimp,
    waves: JSON.parse(JSON.stringify(workout.waves)),
    removedWaves: [],
    waveToEdit: null,
  }
  const [state, dispatch] = useReducer(reducer, initialState)
  const wid = Number(useParams().wid)

  if (state.tag === 'submitted') {
    onSuccess()
    return
  }

  function handleToggleIsAdding() {
    dispatch({ type: 'toggleIsAdding' })
  }

  function onAddWave(maneuvers: Maneuver[], rightSide: boolean) {
    dispatch({ type: 'addWave', wave: { id: null, maneuvers, rightSide } })
  }

  function handleSetWaveToEdit(wave: WaterWorkoutWave) {
    dispatch({ type: 'setWaveToEdit', wave })
  }

  function handleUpdateWave(maneuvers: Maneuver[], rightSide: boolean) {
    if (state.tag !== 'editing' || !state.waveToEdit) return
    dispatch({ type: 'updateWave', wave: { ...state.waveToEdit, maneuvers, rightSide } })
  }

  function handleDeleteWave(id: number, tempId?: number) {
    if (confirm('Are you sure you want to delete this wave?')) dispatch({ type: 'deleteWave', id, tempId })
  }

  function handleSetWaves(waves: WaterWorkoutWave[]) {
    dispatch({ type: 'setWaves', waves })
  }

  function handleOnChange(event: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = event.target
    dispatch({ type: 'setValue', name, value })
  }

  async function handleOnSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()

    if (state.tag !== 'editing') return

    const date = state.date === epochConverter(workout.date, 'yyyy-mm-dd') ? null : state.date
    const condition = state.condition === workout.condition ? null : state.condition
    const rpe = state.rpe === workout.rpe ? null : state.rpe
    const duration = state.duration === workout.duration ? null : state.duration
    const trimp = state.trimp === workout.trimp ? null : state.trimp

    const waves =
      diffListOrNull(state.waves, (wave, index) => {
        const original = workout.waves.find(w => w.id === wave.id)

        const newWave = {
          id: wave.id,
          rightSide: WorkoutEditing.onlyIfDifferent('rightSide', wave, original || {}),
          maneuvers: diffListOrNull(wave.maneuvers, (maneuver, mIndex) => {
            const originalManeuver = original?.maneuvers.find(m => m.id === maneuver.id) || {}
            if (WorkoutEditing.checkDeleteObject(maneuver)) return maneuver

            const newManeuver = {
              id: maneuver.id,
              waterManeuverId: WorkoutEditing.onlyIfDifferent('waterManeuverId', maneuver, originalManeuver),
              success: WorkoutEditing.onlyIfDifferent('success', maneuver, originalManeuver),
              order: WorkoutEditing.checkOrder(mIndex, maneuver.order),
            }

            return WorkoutEditing.noEditingMade(newManeuver) ? null : newManeuver
          }),
          order: WorkoutEditing.checkOrder(index, wave.order),
        }

        return WorkoutEditing.noEditingMade(newWave) ? null : newWave
      }) ?? []

    try {
      await updateWaterActivity(wid, date, condition, rpe, duration, trimp, [...waves, ...state.removedWaves])
      onSuccess()
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error.res) })
    }
  }

  const isAdding = state.isAdding
  const date = state.date
  const condition = state.condition
  const rpe = state.rpe
  const duration = state.duration
  const trimp = state.trimp
  const waves = state.waves
  const waveToEdit = state.waveToEdit
  const error = state.tag === 'editing' ? state.error : undefined
  const disabled =
    state.tag !== 'editing' ||
    state.waves.length === 0 ||
    date.length === 0 ||
    duration === 0 ||
    (date === initialState.date &&
      condition === initialState.condition &&
      rpe === initialState.rpe &&
      trimp === initialState.trimp &&
      duration === initialState.duration &&
      JSON.stringify(state.waves) === JSON.stringify(initialState.waves))

  return (
    <>
      <Popup
        title="Edit Workout"
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
                  onChange={(_, value) => handleOnChange({ target: { name: 'rpe', value } } as any)}
                  min={1}
                  max={10}
                  step={1}
                  marks
                  valueLabelDisplay="auto"
                />
              </div>
              <CustomTimePicker
                defaultValue={duration}
                onChange={value => handleOnChange({ target: { name: 'duration', value } } as any)}
              />
              <div className={styles.sliderContainer}>
                <label>TRIMP</label>
                <Slider
                  name="trimp"
                  value={trimp}
                  min={50}
                  max={190}
                  onChange={(_, value) => handleOnChange({ target: { name: 'trimp', value } } as any)}
                  step={1}
                  defaultValue={70}
                  valueLabelDisplay="auto"
                />
              </div>
              <div className={styles.wavesContainer}>
                <ReorderableList<WaterWorkoutWave>
                  list={waves}
                  renderItem={info => (
                    <div key={info.id || info.tempId}>
                      <p>Side: {info.rightSide ? '➡️' : '⬅️'}</p>
                      {info.maneuvers.map(m => {
                        if (WorkoutEditing.checkDeleteObject(m)) return null

                        return (
                          <p key={m.id || m.tempId}>
                            {m.name} {m.success ? '✅' : '❌'}
                          </p>
                        )
                      })}
                    </div>
                  )}
                  onReorder={handleSetWaves}
                  onClick={item => handleSetWaveToEdit(item)}
                  onDelete={item => handleDeleteWave(item.id, item.tempId)}
                  onAdd={handleToggleIsAdding}
                />
              </div>
              <Button text="Save" type="submit" disabled={disabled} width="100%" height="30px" />
            </form>
            {error && <p className={styles.error}>{error}</p>}
          </>
        }
        onClose={onClose}
      />

      {isAdding && (
        <AddWavePopup
          onAdd={(maneuvers, rightSide) =>
            onAddWave(
              maneuvers.map(m => ({
                id: null,
                waterManeuverId: m.maneuver.id,
                name: m.maneuver.name,
                url: m.maneuver.url,
                success: m.success,
              })),
              rightSide
            )
          }
          onClose={handleToggleIsAdding}
        />
      )}

      {waveToEdit && <EditWavePopup wave={waveToEdit} onClose={() => handleSetWaveToEdit(null)} onSave={handleUpdateWave} />}
    </>
  )
}
