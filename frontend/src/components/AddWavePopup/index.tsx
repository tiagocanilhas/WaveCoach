import * as React from 'react'
import { useReducer } from 'react'

import { Popup } from '../Popup'
import { Card } from '../Card'
import { Button } from '../Button'
import { SelectManeuverPopup } from '../SelectManeuverPopup'

import { WaterManeuver } from '../../types/WaterManeuver'
import { ManeuverToAdd } from '../../types/ManeuverToAdd'
import { WaveToAdd } from '../../types/WaveToAdd'

import styles from './styles.module.css'
import { VerticalReorderableList } from '../VerticalReordabelList'
import { LabeledSwitch } from '../LabeledSwitch'
import { AddManeuverPopup } from '../AddManeuverPopup'

type State = {
  isSelecting: boolean
  rightSide: boolean
  maneuverToEdit: ManeuverToAdd | null
  maneuvers: ManeuverToAdd[] | undefined
}

type Action = 
| { type: 'toggleIsSelecting' }
| { type: 'toggleRightSide' }
| { type: 'addManeuver'; maneuver: ManeuverToAdd }
| { type: 'setManeuverToEdit'; maneuver: ManeuverToAdd | null }
| { type: 'updateManeuver'; maneuver: ManeuverToAdd }
| { type: 'deleteManeuver'; id: string }
| { type: 'setManeuvers'; maneuvers: ManeuverToAdd[] }

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
      return {...state, maneuverToEdit: action.maneuver }
    case 'updateManeuver':
      return {
        ...state,
        maneuvers: state.maneuvers.map(m => m.tempId === action.maneuver.tempId ? action.maneuver : m),
        maneuverToEdit: null,
      }
    case 'deleteManeuver':
      return { ...state, maneuvers: state.maneuvers.filter(m => m.tempId !== action.id) }
    case 'setManeuvers':
      return { ...state, maneuvers: action.maneuvers }
    default:
      return state
  }
}

type AddWavePopupProps = {
  data?: { maneuvers: ManeuverToAdd[], rightSide: boolean }
  onAdd: (maneuvers: ManeuverToAdd[], rightSide: boolean) => void
  onClose: () => void
}

export function AddWavePopup({ data, onClose, onAdd }: AddWavePopupProps) {
  const initialState: State = {   
    isSelecting: false,
    rightSide: data?.rightSide ?? false,
    maneuverToEdit: null,
    maneuvers: data?.maneuvers ?? [],
  }
  const [state, dispatch] = useReducer(reducer, initialState)

  function handleToggleSelect() {
    dispatch({ type: 'toggleIsSelecting' })
  }

  function handleToggleRightSide() {
    dispatch({ type: 'toggleRightSide' })
  }

  function handleOnAdd(maneuver: WaterManeuver, success: boolean) {
    dispatch({ type: 'addManeuver', maneuver: { tempId: Date.now().toString(), maneuver, success } })
  }

  function handleOnEdit(maneuver: ManeuverToAdd) {
    dispatch({ type: 'setManeuverToEdit', maneuver })
  }

  function handleOnUpdate(maneuver: WaterManeuver, success: boolean) {
    dispatch({ type: 'updateManeuver', maneuver: { ...state.maneuverToEdit, maneuver, success } })
  }

  function handleOnDelete(id: string) {
    dispatch({ type: 'deleteManeuver', id })
  }

  function handleOnReorder(newList: ManeuverToAdd[]) {
    dispatch({ type: 'setManeuvers', maneuvers: newList })
  }

  function handleOnClick() {
    onAdd(state.maneuvers, state.rightSide)
  }

  const maneuvers = state.maneuvers
  const disabled = state.maneuvers.length === 0

  return (
    <>
      <Popup
        title={data ? 'Edit Wave' : 'Add Wave'}
        content={
          <div className={styles.addWave}>
            <div className={styles.maneuversContainer}>
              <LabeledSwitch leftLabel='Left Side' rightLabel='Right Side' onChange={handleToggleRightSide} checked={state.rightSide} />
              <VerticalReorderableList<ManeuverToAdd>
                list={maneuvers}
                renderItem={info => (
                  <p>
                    {info.maneuver.name} - {info.success ? '✅' : '❌'}
                  </p>
                )}
                onReorder={handleOnReorder}
                onClick={info => handleOnEdit(info)}
                onDelete={info => handleOnDelete(info.tempId)}
                onAdd={handleToggleSelect}
              />
            </div>
            <Button text={data ? 'Edit' : 'Add'} onClick={handleOnClick} disabled={disabled} width="100%" height="30px" />
          </div>
        }
        onClose={onClose}
      />

      {state.isSelecting && <SelectManeuverPopup onClose={handleToggleSelect} onAdd={handleOnAdd} />}

      {state.maneuverToEdit && <AddManeuverPopup data={state.maneuverToEdit} maneuver={state.maneuverToEdit.maneuver} onAdd={handleOnUpdate} onClose={() => handleOnEdit(null)} />}
    </>
  )
}
