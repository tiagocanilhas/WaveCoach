import * as React from 'react'
import { useReducer } from 'react'
import { useParams } from 'react-router-dom'

import { Characteristics } from '../../pages/Characterisitcs/index'
import { Popup } from '../Popup'
import { Button } from '../Button'

import styles from './styles.module.css'
import { TextField } from '@mui/material'
import { deleteCharacteristics, updateCharacteristics } from '../../services/athleteServices'
import { handleError } from '../../utils/handleError'

type State =
  | { tag: 'showing'; error?: string; values: Characteristics }
  | { tag: 'editing'; error?: string; inputs: Characteristics }
  | { tag: 'submitting'; inputs: Characteristics }

type Action =
  | { type: 'goToEdit' }
  | { type: 'goToShow' }
  | { type: 'edit'; name: string; value: string | number }
  | { type: 'submit' }
  | { type: 'error'; error: string }
  | { type: 'success' }

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'showing':
      switch (action.type) {
        case 'goToEdit':
          return { tag: 'editing', inputs: state.values }
        default:
          return state
      }

    case 'editing':
      switch (action.type) {
        case 'edit':
          return { tag: 'editing', error: undefined, inputs: { ...state.inputs, [action.name]: action.value } }
        case 'submit':
          return { tag: 'submitting', inputs: state.inputs }
        case 'goToShow':
          return { tag: 'showing', values: state.inputs }
        default:
          return state
      }

    case 'submitting':
      switch (action.type) {
        case 'error':
          return { tag: 'showing', error: action.error, values: state.inputs }
        case 'success':
          return { tag: 'showing', values: state.inputs }
        default:
          return state
      }
  }
}

type ShowSelectedCharacteristicsPopupProps = {
  data?: Characteristics
  onClose: () => void
  onSuccess?: () => void
}

export function ShowSelectedCharacteristicsPopup({ data, onClose, onSuccess }: ShowSelectedCharacteristicsPopupProps) {
  const [state, dispatch] = useReducer(reducer, { tag: 'showing', values: data || {} })
  const id = useParams().aid as string

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    if (state.tag !== 'editing') return

    dispatch({ type: 'submit' })
    const { date, height, weight, calories, bodyFat, waistSize, armSize, thighSize, tricepFat, abdomenFat, thighFat } =
      state.inputs

    try {
      await updateCharacteristics(
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
  function handleEdit(ev: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', name: ev.currentTarget.name, value: ev.currentTarget.value })
  }

  function handleUpdate() {
    dispatch({ type: 'goToEdit' })
  }

  function handleShow() {
    dispatch({ type: 'goToShow' })
  }

  async function handleDelete() {
    if (confirm('Are you sure you want to delete this characteristic?')) {
      try {
        await deleteCharacteristics(id, data?.date)
        alert('Characteristic deleted successfully')
        onSuccess()
        onClose()
      } catch (error) {
        alert('Error deleting characteristic')
      }
    }
  }

  return (
    <Popup
      title={`Selected Characteristics: ${data.date || 'N/A'}`}
      content={
        <div className={styles.characteristics}>
          {state.tag === 'showing' && <Showing data={state.values} handleUpdate={handleUpdate} handleDelete={handleDelete} />}
          {state.tag === 'editing' && (
            <Editing data={state.inputs} handleEdit={handleEdit} handleUpdate={handleShow} handleSubmit={handleSubmit} />
          )}
        </div>
      }
      onClose={onClose}
    />
  )
}

type ShowingProps = {
  data: Characteristics
  handleUpdate: () => void
  handleDelete: () => void
}

function Showing({ data, handleUpdate, handleDelete }: ShowingProps) {
  return (
    <form className={styles.form}>
      <TextField label="Height" value={data.height || 'N/A'} inputProps={{ readOnly: true }} />
      <TextField label="Weight" value={data.weight || 'N/A'} inputProps={{ readOnly: true }} />
      <TextField label="Calories" value={data.calories || 'N/A'} inputProps={{ readOnly: true }} />
      <TextField label="Body Fat" value={data.bodyFat || 'N/A'} inputProps={{ readOnly: true }} />
      <TextField label="Waist Size" value={data.waistSize || 'N/A'} inputProps={{ readOnly: true }} />
      <TextField label="Arm Size" value={data.armSize || 'N/A'} inputProps={{ readOnly: true }} />
      <TextField label="Thigh Size" value={data.thighSize || 'N/A'} inputProps={{ readOnly: true }} />
      <TextField label="Tricep Fat" value={data.tricepFat || 'N/A'} inputProps={{ readOnly: true }} />
      <TextField label="Abdomen Fat" value={data.abdomenFat || 'N/A'} inputProps={{ readOnly: true }} />
      <TextField label="Thigh Fat" value={data.thighFat || 'N/A'} inputProps={{ readOnly: true }} />

      <div className={styles.actions}>
        <Button text="Update" onClick={handleUpdate} width="100%" height="25px" />
        <Button text="Delete" onClick={handleDelete} width="100%" height="25px" />
      </div>
    </form>
  )
}

type EditingProps = {
  data: Characteristics
  handleEdit: (ev: React.ChangeEvent<HTMLInputElement>) => void
  handleUpdate: () => void
  handleSubmit: (e: React.FormEvent<HTMLFormElement>) => void
}

function Editing({ data, handleEdit, handleUpdate, handleSubmit }: EditingProps) {
  return (
    <form onSubmit={handleSubmit} className={styles.form}>
      <TextField label="Height" value={data.height} onChange={handleEdit} name="height" type="number" />
      <TextField label="Weight" value={data.weight} onChange={handleEdit} name="weight" type="number" />
      <TextField label="Calories" value={data.calories} onChange={handleEdit} name="calories" type="number" />
      <TextField label="Body Fat" value={data.bodyFat} onChange={handleEdit} name="bodyFat" type="number" />
      <TextField label="Waist Size" value={data.waistSize} onChange={handleEdit} name="waistSize" type="number" />
      <TextField label="Arm Size" value={data.armSize} onChange={handleEdit} name="armSize" type="number" />
      <TextField label="Thigh Size" value={data.thighSize} onChange={handleEdit} name="thighSize" type="number" />
      <TextField label="Tricep Fat" value={data.tricepFat} onChange={handleEdit} name="tricepFat" type="number" />
      <TextField label="Abdomen Fat" value={data.abdomenFat} onChange={handleEdit} name="abdomenFat" type="number" />
      <TextField label="Thigh Fat" value={data.thighFat} onChange={handleEdit} name="thighFat" type="number" />

      <div className={styles.actions}>
        <Button text="Confirm" type="submit" width="100%" height="25px" />
        <Button text="Cancel" onClick={handleUpdate} width="100%" height="25px" />
      </div>
    </form>
  )
}
