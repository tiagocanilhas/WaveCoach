import * as React from 'react'
import { useReducer } from 'react'

import { Popup } from '../Popup'
import { Button } from '../Button'
import { SelectManeuverPopup } from '../SelectManeuverPopup'
import { ReorderableList } from '../ReorderableList'
import { LabeledSwitch } from '../LabeledSwitch'
import { WaterWorkoutWave } from '../../types/WaterWorkoutWave'
import { EditManeuverPopup } from '../EditManeuverPopup'

import { WaterManeuver } from '../../types/WaterManeuver'
import { Maneuver } from '../../types/Maneuver'

import styles from './styles.module.css'
import { WorkoutEditing } from '../../../../utils/WorkoutEditing'

type State = {
  isSelecting: boolean
  rightSide: boolean
  maneuverToEdit: Maneuver | null
  maneuvers: Maneuver[] | undefined
  removedManeuvers: Maneuver[]
}

type Action =
  | { type: 'toggleIsSelecting' }
  | { type: 'toggleRightSide' }
  | { type: 'addManeuver'; maneuver: Maneuver }
  | { type: 'setManeuverToEdit'; maneuver: Maneuver | null }
  | { type: 'updateManeuver'; maneuver: Maneuver }
  | { type: 'deleteManeuver'; id: number; tempId?: number }
  | { type: 'setManeuvers'; maneuvers: Maneuver[] }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'toggleIsSelecting':
      return { ...state, isSelecting: !state.isSelecting }
    case 'toggleRightSide':
      return { ...state, rightSide: !state.rightSide }
    case 'addManeuver':
      return {
        ...state,
        isSelecting: false,
        maneuvers: [...state.maneuvers, action.maneuver],
      }
    case 'setManeuverToEdit':
      return { ...state, maneuverToEdit: action.maneuver }
    case 'updateManeuver':
      return {
        ...state,
        maneuvers: state.maneuvers.map(m => {
          const matchById = m.id !== null && m.id === action.maneuver.id
          const matchByTempId = m.id === null && m.tempId === action.maneuver.tempId
          return matchById || matchByTempId ? action.maneuver : m
        }),
        maneuverToEdit: null,
      }
    case 'deleteManeuver':
      const removedManeuver = state.maneuvers.find(maneuver => maneuver.id !== null ? maneuver.id === action.id : maneuver.tempId === action.tempId)
      return {
        ...state,
        maneuvers: state.maneuvers.filter(m => m.id !== null ? m.id !== action.id : m.tempId !== action.tempId),
        removedManeuvers: [...state.removedManeuvers, WorkoutEditing.nullifyFieldsExceptId(removedManeuver)],
      }
    case 'setManeuvers':
      return { ...state, maneuvers: action.maneuvers }
    default:
      return state
  }
}

type EditWavePopupProps = {
  wave?: WaterWorkoutWave
  onSave: (maneuvers: Maneuver[], rightSide: boolean) => void
  onClose: () => void
}

export function EditWavePopup({ wave, onClose, onSave }: EditWavePopupProps) {
  const initialState: State = {
    rightSide: wave ? wave.rightSide : false,
    maneuvers: wave ? wave.maneuvers : [],
    removedManeuvers: [],
    isSelecting: false,
    maneuverToEdit: null,
  }
  const [state, dispatch] = useReducer(reducer, initialState)

  function handleToggleSelect() {
    dispatch({ type: 'toggleIsSelecting' })
  }

  function handleToggleRightSide() {
    dispatch({ type: 'toggleRightSide' })
  }

  function handleOnSave(maneuver: WaterManeuver, success: boolean) {
    const newManeuver: Maneuver = {
      id: null,
      tempId: Date.now(), // Temporary ID for new maneuvers
      waterManeuverId: maneuver.id,
      name: maneuver.name,
      url: maneuver.url,
      success,
      order: state.maneuvers.length + 1,
    }
    dispatch({ type: 'addManeuver', maneuver: newManeuver })
  }

  function handleOnEdit(maneuver: Maneuver) {
    dispatch({ type: 'setManeuverToEdit', maneuver })
  }

  function handleOnUpdate(maneuver: Maneuver) {
    dispatch({ type: 'updateManeuver', maneuver })
  }

  function handleOnDelete(id: number, tempId?: number) {
    if (confirm('Are you sure you want to delete this maneuver?')) dispatch({ type: 'deleteManeuver', id, tempId })
  }

  function handleOnReorder(newList: Maneuver[]) {
    dispatch({ type: 'setManeuvers', maneuvers: newList })
  }

  function handleOnClick() {
    onSave([...state.maneuvers, ...state.removedManeuvers], state.rightSide)
  }

  const maneuvers = state.maneuvers
  const disabled = state.maneuvers.length === 0

  return (
    <>
      <Popup
        title="Edit Wave"
        content={
          <div className={styles.addWave}>
            <div className={styles.maneuversContainer}>
              <LabeledSwitch
                leftLabel="Left Side"
                rightLabel="Right Side"
                onChange={handleToggleRightSide}
                checked={state.rightSide}
              />
              <ReorderableList<Maneuver>
                list={maneuvers}
                renderItem={info => (
                  <p>
                    {info.name} - {info.success ? '✅' : '❌'}
                  </p>
                )}
                onReorder={handleOnReorder}
                onClick={info => handleOnEdit(info)}
                onDelete={info => handleOnDelete(info.id, info.tempId)}
                onAdd={handleToggleSelect}
              />
            </div>
            <Button text="Save" onClick={handleOnClick} disabled={disabled} width="100%" height="30px" />
          </div>
        }
        onClose={onClose}
      />

      {state.isSelecting && <SelectManeuverPopup onClose={handleToggleSelect} onAdd={handleOnSave} />}

      {state.maneuverToEdit && (
        <EditManeuverPopup maneuver={state.maneuverToEdit} onSave={handleOnUpdate} onClose={() => handleOnEdit(null)} />
      )}
    </>
  )
}
