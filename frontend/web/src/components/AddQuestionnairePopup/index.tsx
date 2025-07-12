import * as React from 'react'
import { FormEvent, useReducer } from 'react'
import { useParams } from 'react-router-dom'

import { Popup } from '../Popup'
import { Slider } from '@mui/material'
import { Button } from '../Button'

import { handleError } from '../../../../utils/handleError'

import { createQuestionnaire } from '../../../../services/waterServices'

import styles from './styles.module.css'

type State =
  | { tag: 'editing'; sleep: number; fatigue: number; stress: number; musclePain: number; error?: string }
  | { tag: 'submitting'; sleep: number; fatigue: number; stress: number; musclePain: number; error?: string }
  | { tag: 'submitted' }
type Action = { type: 'setSlider'; name: string; value: number } | { type: 'error'; error: string } | { type: 'submit' }

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'editing':
      switch (action.type) {
        case 'setSlider':
          return { ...state, [action.name]: action.value, error: undefined }
        case 'submit':
          return { ...state, tag: 'submitting' }
        default:
          return state
      }
    case 'submitting':
      switch (action.type) {
        case 'error':
          return { ...state, tag: 'editing', error: action.error }
        default:
          return state
      }
  }
}

type AddQuestionnairePopupProps = {
  onClose: () => void
  onSuccess: () => void
}

export function AddQuestionnairePopup({ onClose, onSuccess }: AddQuestionnairePopupProps) {
  const initialState: State = { tag: 'editing', sleep: 0, fatigue: 0, stress: 0, musclePain: 0, error: undefined }
  const [state, dispatch] = useReducer(reducer, initialState)
  const wid = Number(useParams().wid)

  function handleSliderChange(event: Event, newValue: number) {
    const target = event.target as HTMLInputElement
    dispatch({ type: 'setSlider', name: target.name, value: newValue })
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()

    if (state.tag !== 'editing') return

    const { sleep, fatigue, stress, musclePain } = state

    dispatch({ type: 'submit' })

    try {
      await createQuestionnaire(wid, sleep, fatigue, stress, musclePain)
      onSuccess()
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error.res) })
    }
  }

  const sleep = state.tag !== 'submitted' ? state.sleep : 0
  const fatigue = state.tag !== 'submitted' ? state.fatigue : 0
  const stress = state.tag !== 'submitted' ? state.stress : 0
  const musclePain = state.tag !== 'submitted' ? state.musclePain : 0
  const disabled = state.tag === 'submitting'

  return (
    <Popup
      title="Add Questionnaire"
      content={
        <form onSubmit={handleSubmit} className={styles.container}>
          {[
            { value: sleep, label: 'Sleep' },
            { value: fatigue, label: 'Fatigue' },
            { value: stress, label: 'Stress' },
            { value: musclePain, label: 'Muscle Pain' },
          ].map(({ value, label }) => (
            <div key={label} className={styles.sliderContainer}>
              <label>{label}</label>
              <Slider
                name={label.charAt(0).toLowerCase() + label.replace(/\s/g, '').slice(1)}
                value={value}
                onChange={handleSliderChange}
                min={1}
                max={5}
                step={1}
                marks
                valueLabelDisplay="auto"
              />
            </div>
          ))}
          <Button text="Add" type="submit" width="100%" height="30px" disabled={disabled} />
          {state.tag === 'editing' && <p className={styles.errorMessage}>{state.error}</p>}
        </form>
      }
      onClose={onClose}
    />
  )
}
