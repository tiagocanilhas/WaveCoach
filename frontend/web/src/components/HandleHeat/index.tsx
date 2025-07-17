import * as React from 'react'
import { useEffect, useReducer, useState } from 'react'

import { TextField } from '@mui/material'
import { ReorderableList } from '../ReorderableList'
import { HandleHeatWave } from '../HandleHeatWave'
import { EditWavePopup } from '../EditWavePopup'

import { WorkoutEditing } from '../../../../utils/WorkoutEditing'

import { Heat } from '../../types/Heat'
import { WaterWorkoutWave } from '../../types/WaterWorkoutWave'
import { Maneuver } from '../../types/Maneuver'

import styles from './styles.module.css'

type State = {
  points: number
  isAdding: boolean
  waves: WaterWorkoutWave[]
  removedWaves: WaterWorkoutWave[]
  waveToEdit?: WaterWorkoutWave
}

type Action =
  | { type: 'edit'; name: string; value: string | number }
  | { type: 'setWaves'; waves: WaterWorkoutWave[] }
  | { type: 'toggleIsAdding' }
  | { type: 'addWave'; wave: WaterWorkoutWave }
  | { type: 'setWaveToEdit'; wave: WaterWorkoutWave }
  | { type: 'updateWave'; wave: WaterWorkoutWave }
  | { type: 'removeWave'; id: number; tempId: number }
  | { type: 'setWavePoints'; id: number; tempId?: number; points: number }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'edit':
      return { ...state, [action.name]: action.value }
    case 'setWaves':
      return { ...state, waves: action.waves }
    case 'toggleIsAdding':
      return { ...state, isAdding: !state.isAdding }
    case 'addWave':
      return {
        ...state,
        waves: [...state.waves, action.wave],
        isAdding: false,
      }
    case 'setWaveToEdit':
      return { ...state, waveToEdit: action.wave }
    case 'updateWave':
      return {
        ...state,
        waves: state.waves.map(w => {
          const matchById = w.id !== null && w.id === action.wave.id
          const matchByTempId = w.id === null && w.tempId === action.wave.tempId
          return matchById || matchByTempId ? action.wave : w
        }),
        waveToEdit: undefined,
      }
    case 'removeWave':
      const waveToRemove = state.waves.find(w => (w.id !== null ? w.id === action.id : w.tempId === action.tempId))
      return {
        ...state,
        waves: state.waves.filter(w => (w.id !== null ? w.id !== action.id : w.tempId !== action.tempId)),
        removedWaves: [...state.removedWaves, WorkoutEditing.nullifyFieldsExceptId(waveToRemove)],
      }
    case 'setWavePoints':
      return {
        ...state,
        waves: state.waves.map(wave => {
          const matchById = wave.id !== null && wave.id === action.id
          const matchByTempId = wave.id === null && wave.tempId === action.tempId
          return matchById || matchByTempId ? { ...wave, points: action.points } : wave
        }),
      }
    default:
      return state
  }
}

type HandleHeatProps = {
  heat?: Heat
  setHeat: (heat: Heat) => void
}

export function HandleHeat({ heat, setHeat }: HandleHeatProps) {
  const initialState: State = {
    points: heat ? heat.score : 0,
    waves: JSON.parse(JSON.stringify(heat ? heat.waterActivity.waves : [])),
    removedWaves: [],
    isAdding: false,
  }
  const [state, dispatch] = useReducer(reducer, initialState)

  function handleReorder(waves: WaterWorkoutWave[]) {
    dispatch({ type: 'setWaves', waves })
  }

  function handleOnChange(event: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = event.target
    dispatch({ type: 'edit', name, value })
  }

  function handleToggleIsAdding() {
    dispatch({ type: 'toggleIsAdding' })
  }

  function handleWaveToEdit(wave: WaterWorkoutWave) {
    dispatch({ type: 'setWaveToEdit', wave })
  }

  function handleAddWave(maneuvers: Maneuver[], rightSide: boolean) {
    const wave: WaterWorkoutWave = {
      id: null,
      tempId: Date.now(), // Temporary ID for new waves
      points: 0,
      rightSide,
      maneuvers,
    }
    dispatch({ type: 'addWave', wave })
    setHeat({ ...heat, waterActivity: { ...heat.waterActivity, waves: [...state.waves, wave] } })
  }

  function handleUpdateWave(maneuvers: Maneuver[], rightSide: boolean) {
    dispatch({ type: 'updateWave', wave: { ...state.waveToEdit, maneuvers, rightSide } })
  }

  function handleOnDeleteWave(id: number, tempId: number) {
    if (confirm('Are you sure you want to delete this wave?')) {
      dispatch({ type: 'removeWave', id, tempId })
    }
  }

  function handleSetWavePoints(points: number, id: number, tempId?: number) {
    dispatch({ type: 'setWavePoints', id, tempId, points })
  }

  const points = state.points
  const waves = state.waves

  return (
    <>
      <div className={styles.container}>
        <TextField name="points" type="number" label="Points" value={points} onChange={handleOnChange} />
        <ReorderableList
          list={waves}
          renderItem={info => (
            <HandleHeatWave wave={info} setPoints={points => handleSetWavePoints(points, info.id, info.tempId)} />
          )}
          onReorder={handleReorder}
          onClick={handleWaveToEdit}
          onDelete={wave => handleOnDeleteWave(wave.id, wave.tempId)}
          onAdd={handleToggleIsAdding}
          cardSize="400px"
        />
      </div>

      {state.isAdding && <EditWavePopup onClose={handleToggleIsAdding} onSave={handleAddWave} />}

      {state.waveToEdit && (
        <EditWavePopup wave={state.waveToEdit} onClose={() => handleWaveToEdit(undefined)} onSave={handleUpdateWave} />
      )}
    </>
  )
}
