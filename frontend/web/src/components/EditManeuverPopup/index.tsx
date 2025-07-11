import * as React from 'react'
import { useReducer, useState } from 'react'

import { Popup } from '../Popup'
import { Button } from '../Button'

import styles from './styles.module.css'
import { LabeledSwitch } from '../LabeledSwitch'
import { Maneuver } from '../../types/Maneuver'

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

type EditManeuverPopupProps = {
  maneuver: Maneuver
  onSave: (maneuver: Maneuver) => void
  onClose: () => void
}

export function EditManeuverPopup({ maneuver, onSave, onClose }: EditManeuverPopupProps) {
  const initialState: State = { success: maneuver.success }
  const [state, dispatch] = useReducer(reducer, initialState)

  function toggleSuccess() {
    dispatch({ type: 'toggleSuccess' })
  }
  const success = state.success
  const disabled = false

  function handleAddExercise() {
    onSave({ ...maneuver, success })
    onClose()
  }

  return (
    <Popup
      title={'Edit Maneuver'}
      content={
        <div className={styles.container}>
          <div className={styles.maneuver}>
            <img src={maneuver.url || '/images/no_image.svg'} alt="Exercise" />
            <h2>{maneuver.name}</h2>
          </div>

          <LabeledSwitch leftLabel="Failed" rightLabel="Succeded" checked={success} onChange={toggleSuccess} />

          <Button text="Save" disabled={disabled} onClick={handleAddExercise} width="100%" height="30px" />
        </div>
      }
      onClose={onClose}
    />
  )
}
