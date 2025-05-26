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

type State = {
  isOpen: boolean
  maneuvers: ManeuverToAdd[] | undefined
}

type Action = { type: 'openPopup' } | { type: 'closePopup' } | { type: 'addManeuver'; maneuver: ManeuverToAdd }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'openPopup':
      return { ...state, isOpen: true }
    case 'closePopup':
      return { ...state, isOpen: false }
    case 'addManeuver':
      return {
        ...state,
        isOpen: false,
        maneuvers: [...state.maneuvers, action.maneuver],
      }
    default:
      return state
  }
}

type AddWavePopupProps = {
  onAdd: (wave: WaveToAdd) => void
  onClose: () => void
}

const initialState: State = { isOpen: false, maneuvers: [] }

export function AddWavePopup({ onClose, onAdd }: AddWavePopupProps) {
  const [state, dispatch] = useReducer(reducer, initialState)

  function handleAddManuever() {
    dispatch({ type: 'openPopup' })
  }

  function handleOnClose() {
    dispatch({ type: 'closePopup' })
  }

  function handleOnAdd(maneuver: WaterManeuver, rightSide: boolean, success: boolean) {
    const maneuverToAdd: ManeuverToAdd = {
      waterManeuverId: maneuver.id,
      name: maneuver.name,
      rightSide: rightSide,
      success: success,
    }
    dispatch({ type: 'addManeuver', maneuver: maneuverToAdd })
  }

  function handleOnClick() {
    onAdd({ maneuvers: state.maneuvers })
  }

  const maneuvers = state.maneuvers
  const disabled = state.maneuvers.length === 0

  return (
    <>
      <Popup
        title="Add Wave"
        content={
          <div className={styles.addWave}>
            <div className={styles.maneuversContainer}>
              {maneuvers.map(info => (
                <Card
                  content={
                    <p>
                      {info.name} - {info.rightSide ? '➡️' : '⬅️'} {info.success ? '✅' : '❌'}
                    </p>
                  }
                />
              ))}
              <Card
                content={
                  <div className={styles.add} onClick={handleAddManuever}>
                    +
                  </div>
                }
                width="600px"
              />
            </div>
            <Button text="Add" onClick={handleOnClick} disabled={disabled} width="100%" height="30px" />
          </div>
        }
        onClose={onClose}
      />

      {state.isOpen && <SelectManeuverPopup onClose={handleOnClose} onAdd={handleOnAdd} />}
    </>
  )
}
