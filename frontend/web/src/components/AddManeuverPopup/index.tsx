import * as React from 'react'
import { useReducer, useState } from 'react'

import { Popup } from '../Popup'
import { Button } from '../Button'

import { WaterManeuver } from '../../types/WaterManeuver'

import styles from './styles.module.css'
import { LabeledSwitch } from '../LabeledSwitch'

type State = {
  success: boolean
}

type Action = { type: 'toggleSuccess' }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'toggleSuccess':
      return { ...state, success: !state.success }
    default:
      return state
  }
}

type AddManeuverPopupProps = {
  data?: { success: boolean }
  maneuver: WaterManeuver
  onAdd: (maneuver: WaterManeuver, success: boolean) => void
  onClose: () => void
}

export function AddManeuverPopup({ data, maneuver, onAdd, onClose }: AddManeuverPopupProps) {
  const initialState: State = { success: data?.success ?? true }
  const [state, dispatch] = useReducer(reducer, initialState)

  function toggleSuccess() {
    dispatch({ type: 'toggleSuccess' })
  }
  const success = state.success
  const disabled = false

  function handleAddExercise() {
    onAdd(maneuver, success)
    onClose()
  }

  return (
    <Popup
      title={data ? 'Edit Maneuver' : 'Add Maneuver'}
      content={
        <div className={styles.container}>
          <div className={styles.maneuver}>
            <img src={maneuver.url || '/images/no_image.svg'} alt="Exercise" />
            <h2>{maneuver.name}</h2>
          </div>

          <LabeledSwitch leftLabel="Failed" rightLabel="Succeded" checked={success} onChange={toggleSuccess} />

          <Button text={data ? 'Edit' : 'Add'} disabled={disabled} onClick={handleAddExercise} width="100%" height="30px" />
        </div>
      }
      onClose={onClose}
    />
  )
}
