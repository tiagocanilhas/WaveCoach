import * as React from 'react'
import { useReducer, useState } from 'react'

import { Popup } from '../Popup'
import { Button } from '../Button'
import { Switch, TextField } from '@mui/material'
import { Card } from '../Card'

import { Maneuver } from '../../types/Maneuver'

import styles from './styles.module.css'

type State = {
  isRight: boolean
  success: boolean
}

type Action = { type: 'toggleSide' } | { type: 'toggleSuccess' }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'toggleSide':
      return { ...state, isRight: !state.isRight }
    case 'toggleSuccess':
      return { ...state, success: !state.success }
    default:
      return state
  }
}

type AddManeuverPopupProps = {
  maneuver: Maneuver
  onAdd: (maneuver: Maneuver, isRight: boolean, success: boolean) => void
  onClose: () => void
}

export function AddManeuverPopup({ maneuver, onAdd, onClose }: AddManeuverPopupProps) {
  const initialState: State = { isRight: false, success: true }
  const [state, dispatch] = useReducer(reducer, initialState)

  function toggleSide() {
    dispatch({ type: 'toggleSide' })
  }

  function toggleSuccess() {
    dispatch({ type: 'toggleSuccess' })
  }

  const isRight = state.isRight
  const success = state.success
  const disabled = false

  function handleAddExercise() {
    onAdd(maneuver, isRight, success)
    onClose()
  }

  return (
    <Popup
      title="Add Maneuver"
      content={
        <div className={styles.container}>
          <h2>{maneuver.name}</h2>
          <div className={styles.side}>
            Left
            <Switch checked={isRight} onChange={toggleSide} />
            Right
          </div>
          <div className={styles.success}>
            Failed
            <Switch checked={success} onChange={toggleSuccess} />
            Success
          </div>
          <Button text="Add" disabled={disabled} onClick={handleAddExercise} width="100%" height="30px" />
        </div>
      }
      onClose={onClose}
    />
  )
}
