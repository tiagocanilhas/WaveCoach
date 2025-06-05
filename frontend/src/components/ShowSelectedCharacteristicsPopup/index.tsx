import * as React from 'react'
import { useReducer } from 'react'
import { useParams } from 'react-router-dom'

import { TextField } from '@mui/material'
import { Popup } from '../Popup'
import { Button } from '../Button'

import { Characteristics } from '../../types/Characteristics'

import { deleteCharacteristics, updateCharacteristics } from '../../../services/athleteServices'

import { handleError } from '../../../utils/handleError'

import { useAuthentication } from '../../hooks/useAuthentication'

import styles from './styles.module.css'

type State =
  | { tag: 'showing'; error?: string; values: Characteristics }
  | { tag: 'editing'; error?: string; inputs: Characteristics; oldData: Characteristics }
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
          return { tag: 'editing', inputs: state.values, oldData: state.values }
        default:
          return state
      }

    case 'editing':
      switch (action.type) {
        case 'edit':
          return { ...state, tag: 'editing', error: undefined, inputs: { ...state.inputs, [action.name]: action.value } }
        case 'submit':
          return { tag: 'submitting', inputs: state.inputs }
        case 'goToShow':
          return { tag: 'showing', values: state.oldData, error: undefined }
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
  const initialState: State = { tag: 'showing', values: data || {} }
  const [state, dispatch] = useReducer(reducer, initialState)
  const id = useParams().aid

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
      title="Selected Characteristics"
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
  const [user] = useAuthentication()
  return (
    <form className={styles.form}>
      <div className={styles.row}>
        <div className={styles.column}>
          <TextField value={data.date || 'N/A'} inputProps={{ readOnly: true }} label="Date" InputLabelProps={{ shrink: true }} />
          <TextField label="Height (cm)" value={data.height || 'N/A'} inputProps={{ readOnly: true }} />
          <TextField label="Weight (kg)" value={data.weight || 'N/A'} inputProps={{ readOnly: true }} />
          <TextField label="Calories (kcal)" value={data.calories || 'N/A'} inputProps={{ readOnly: true }} />
          <TextField label="Body Fat (%)" value={data.bodyFat || 'N/A'} inputProps={{ readOnly: true }} />
        </div>
        <div className={styles.column}>
          <TextField label="Waist Size (cm)" value={data.waistSize || 'N/A'} inputProps={{ readOnly: true }} />
          <TextField label="Arm Size (cm)" value={data.armSize || 'N/A'} inputProps={{ readOnly: true }} />
          <TextField label="Thigh Size (cm)" value={data.thighSize || 'N/A'} inputProps={{ readOnly: true }} />
          <TextField label="Tricep Fat (mm)" value={data.tricepFat || 'N/A'} inputProps={{ readOnly: true }} />
          <TextField label="Abdomen Fat (mm)" value={data.abdomenFat || 'N/A'} inputProps={{ readOnly: true }} />
          <TextField label="Thigh Fat (mm)" value={data.thighFat || 'N/A'} inputProps={{ readOnly: true }} />
        </div>
      </div>
      {user.isCoach && (
        <div className={styles.actions}>
          <Button text="Update" onClick={handleUpdate} width="100%" height="25px" />
          <Button text="Delete" onClick={handleDelete} width="100%" height="25px" />
        </div>
      )}
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
  // dd-mm-yyyy to dd/mm/yyyy format conversion
  const dateNewFormat = data.date
    ? new Date(data.date.split('-').reverse().join('-')).toISOString().split('T')[0]
    : new Date().toISOString().split('T')[0]
  return (
    <form onSubmit={handleSubmit} className={styles.form}>
      <div className={styles.row}>
        <div className={styles.column}>
          <TextField
            value={dateNewFormat}
            onChange={handleEdit}
            name="date"
            type="date"
            label="Date"
            InputLabelProps={{ shrink: true }}
          />
          <TextField label="Height (cm)" value={data.height} onChange={handleEdit} name="height" type="number" />
          <TextField label="Weight (kg)" value={data.weight} onChange={handleEdit} name="weight" type="number" />
          <TextField label="Calories (kcal)" value={data.calories} onChange={handleEdit} name="calories" type="number" />
          <TextField label="Body Fat (%)" value={data.bodyFat} onChange={handleEdit} name="bodyFat" type="number" />
        </div>
        <div className={styles.column}>
          <TextField label="Waist Size (cm)" value={data.waistSize} onChange={handleEdit} name="waistSize" type="number" />
          <TextField label="Arm Size (cm)" value={data.armSize} onChange={handleEdit} name="armSize" type="number" />
          <TextField label="Thigh Size (cm)" value={data.thighSize} onChange={handleEdit} name="thighSize" type="number" />
          <TextField label="Tricep Fat (mm)" value={data.tricepFat} onChange={handleEdit} name="tricepFat" type="number" />
          <TextField label="Abdomen Fat (mm)" value={data.abdomenFat} onChange={handleEdit} name="abdomenFat" type="number" />
          <TextField label="Thigh Fat (mm)" value={data.thighFat} onChange={handleEdit} name="thighFat" type="number" />
        </div>
      </div>
      <div className={styles.actions}>
        <Button text="Confirm" type="submit" width="100%" height="25px" />
        <Button text="Cancel" onClick={handleUpdate} width="100%" height="25px" />
      </div>
    </form>
  )
}
