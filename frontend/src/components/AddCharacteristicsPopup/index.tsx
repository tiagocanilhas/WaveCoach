import * as React from 'react'
import { useReducer } from 'react'
import { useParams } from 'react-router-dom'

import TextField from '@mui/material/TextField'
import { Popup } from '../Popup'
import { Button } from '../Button'

import { createCharacteristics } from '../../services/athleteServices'

import { handleError } from '../../utils/handleError'

import styles from './styles.module.css'

type State =
  | {
      tag: 'editing'
      inputs: {
        date?: string
        height?: number
        weight?: number
        calories?: number
        bodyFat?: number
        waistSize?: number
        armSize?: number
        thighSize?: number
        tricepFat?: number
        abdomenFat?: number
        thighFat?: number
      }
      error?: string
    }
  | {
      tag: 'submitting'
      date?: string
      height?: number
      weight?: number
      calories?: number
      bodyFat?: number
      waistSize?: number
      armSize?: number
      thighSize?: number
      tricepFat?: number
      abdomenFat?: number
      thighFat?: number
    }
  | {
      tag: 'submitted'
    }

type Action =
  | { type: 'edit'; name: string; value: string }
  | { type: 'submit' }
  | { type: 'error'; error: string }
  | { type: 'success' }

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'editing':
      switch (action.type) {
        case 'edit':
          return { ...state, inputs: { ...state.inputs, [action.name]: action.value }, error: undefined }
        case 'submit':
          return {
            tag: 'submitting',
            date: state.inputs.date,
            height: state.inputs.height,
            weight: state.inputs.weight,
            calories: state.inputs.calories,
            bodyFat: state.inputs.bodyFat,
            waistSize: state.inputs.waistSize,
            armSize: state.inputs.armSize,
            thighSize: state.inputs.thighSize,
            tricepFat: state.inputs.tricepFat,
            abdomenFat: state.inputs.abdomenFat,
            thighFat: state.inputs.thighFat,
          }
        default:
          return state
      }

    case 'submitting':
      switch (action.type) {
        case 'error':
          return {
            tag: 'editing',
            error: action.error,
            inputs: {
              date: state.date,
              height: state.height,
              weight: state.weight,
              calories: state.calories,
              bodyFat: state.bodyFat,
              waistSize: state.waistSize,
              armSize: state.armSize,
              thighSize: state.thighSize,
              tricepFat: state.tricepFat,
              abdomenFat: state.abdomenFat,
              thighFat: state.thighFat,
            },
          }
        case 'success':
          return {
            tag: 'submitted',
          }
        default:
          return state
      }

    case 'submitted':
      return {
        tag: 'editing',
        inputs: {
          date: '',
          height: undefined,
          weight: undefined,
          calories: undefined,
          bodyFat: undefined,
          waistSize: undefined,
          armSize: undefined,
          thighSize: undefined,
          tricepFat: undefined,
          abdomenFat: undefined,
          thighFat: undefined,
        },
      }
  }
}

type AddCharacteristicsPopupProps = {
  onClose: () => void
  onSuccess: () => void
}

const initialState: State = {
  tag: 'editing',
  inputs: {
    date: '',
    height: undefined,
    weight: undefined,
    calories: undefined,
    bodyFat: undefined,
    waistSize: undefined,
    armSize: undefined,
    thighSize: undefined,
    tricepFat: undefined,
    abdomenFat: undefined,
    thighFat: undefined,
  },
}

export function AddCharacteristicsPopup({ onClose, onSuccess }: AddCharacteristicsPopupProps) {
  const [state, dispatch] = useReducer(reducer, initialState)
  const id = useParams().aid as string

  function handleChange(ev: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', name: ev.target.name, value: ev.target.value })
  }

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    if (state.tag !== 'editing') return

    dispatch({ type: 'submit' })
    const { date, height, weight, calories, bodyFat, waistSize, armSize, thighSize, tricepFat, abdomenFat, thighFat } =
      state.inputs

    try {
      await createCharacteristics(
        id,
        date,
        height,
        weight,
        calories,
        bodyFat,
        waistSize,
        armSize,
        thighSize,
        tricepFat,
        abdomenFat,
        thighFat
      )
      dispatch({ type: 'success' })
      onSuccess()
      onClose()
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error) })
    }
  }

  const date = state.tag === 'editing' ? state.inputs.date : ''
  const height = state.tag === 'editing' ? state.inputs.height : undefined
  const weight = state.tag === 'editing' ? state.inputs.weight : undefined
  const calories = state.tag === 'editing' ? state.inputs.calories : undefined
  const bodyFat = state.tag === 'editing' ? state.inputs.bodyFat : undefined
  const waistSize = state.tag === 'editing' ? state.inputs.waistSize : undefined
  const armSize = state.tag === 'editing' ? state.inputs.armSize : undefined
  const thighSize = state.tag === 'editing' ? state.inputs.thighSize : undefined
  const tricepFat = state.tag === 'editing' ? state.inputs.tricepFat : undefined
  const abdomenFat = state.tag === 'editing' ? state.inputs.abdomenFat : undefined
  const thighFat = state.tag === 'editing' ? state.inputs.thighFat : undefined
  const disabled = state.tag === 'submitting'

  return (
    <Popup
      title="Add Characteristics"
      onClose={onClose}
      content={
        <form onSubmit={handleSubmit} className={styles.form}>
          <TextField name="date" type="date" label="Date" value={date} onChange={handleChange} />
          <TextField name="height" type="number" label="Height" value={height} onChange={handleChange} />
          <TextField name="weight" type="number" label="Weight" value={weight} onChange={handleChange} />
          <TextField name="calories" type="number" label="Calories" value={calories} onChange={handleChange} />
          <TextField name="bodyFat" type="number" label="Body Fat" value={bodyFat} onChange={handleChange} />
          <TextField name="waistSize" type="number" label="Waist Size" value={waistSize} onChange={handleChange} />
          <TextField name="armSize" type="number" label="Arm Size" value={armSize} onChange={handleChange} />
          <TextField name="thighSize" type="number" label="Thigh Size" value={thighSize} onChange={handleChange} />
          <TextField name="tricepFat" type="number" label="Tricep Fat" value={tricepFat} onChange={handleChange} />
          <TextField name="abdomenFat" type="number" label="Abdomen Fat" value={abdomenFat} onChange={handleChange} />
          <TextField name="thighFat" type="number" label="Thigh Fat" value={thighFat} onChange={handleChange} />
          <Button text="Add" type="submit" disabled={disabled} width="100%" height="25px" />
          {state.tag === 'editing' && state.error && <div className={styles.error}>{state.error}</div>}
        </form>
      }
    />
  )
}
