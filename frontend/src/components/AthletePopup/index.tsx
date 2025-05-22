import * as React from 'react'
import { useReducer, useRef } from 'react'

import { TextField } from '@mui/material'
import { Popup } from '../../components/Popup'
import { Button } from '../../components/Button'
import { ImageSelector } from '../ImageSelector'

import { createAthlete, updateAthlete } from '../../services/athleteServices'

import { handleError } from '../../utils/handleError'

import styles from './styles.module.css'

type State =
  | { tag: 'editing'; inputs: { name: string; birthdate: string }; image: File; error?: string }
  | { tag: 'submitting'; name: string; birthdate: string; image: File | null }
  | { tag: 'submitted' }

type Action =
  | { type: 'edit'; name: string; value: string }
  | { type: 'setImage'; image: File }
  | { type: 'submit' }
  | { type: 'error'; error: string }
  | { type: 'success' }

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'editing':
      switch (action.type) {
        case 'edit':
          return { ...state, inputs: { ...state.inputs, [action.name]: action.value }, error: undefined }
        case 'setImage':
          return { ...state, image: action.image }
        case 'submit':
          return { tag: 'submitting', name: state.inputs.name, birthdate: state.inputs.birthdate, image: state.image }
        default:
          return state
      }

    case 'submitting':
      switch (action.type) {
        case 'error':
          return {
            tag: 'editing',
            error: action.error,
            inputs: { name: state.name, birthdate: state.birthdate },
            image: state.image,
          }
        case 'success':
          return { tag: 'submitted' }
        default:
          return state
      }

    case 'submitted':
      return {
        tag: 'editing',
        inputs: { name: '', birthdate: '' },
        image: null,
      }
  }
}

type AthletePopupProps = {
  onClose: () => void
  onSuccess: () => void
  initialValues?: {
    id: number
    name: string
    birthdate: string
  }
}

export function AthletePopup({ onClose, onSuccess, initialValues }: AthletePopupProps) {
  const isEditing = initialValues ? true : false

  const initialRef = useRef(initialValues)

  const initialState: State = {
    tag: 'editing',
    inputs: { name: initialValues?.name || '', birthdate: initialValues?.birthdate || '' },
    image: null,
  }
  const [state, dispatch] = useReducer(reducer, initialState)

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    if (state.tag !== 'editing') return

    dispatch({ type: 'submit' })

    try {
      if (isEditing) await updateAthlete(initialValues.id.toString(), state.inputs.name, state.inputs.birthdate)
      else await createAthlete(state.inputs.name, state.inputs.birthdate, state.image)

      dispatch({ type: 'success' })
      onSuccess()
      onClose()
    } catch (err) {
      dispatch({ type: 'error', error: handleError(err) })
    }
  }

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', name: e.target.name, value: e.target.value })
  }

  function handleImageChange(file: File) {
    dispatch({ type: 'setImage', image: file })
  }

  const name = state.tag === 'editing' ? state.inputs.name : ''
  const birthdate = state.tag === 'editing' ? state.inputs.birthdate : ''
  const disabled =
    state.tag === 'submitting' ||
    name.trim() === '' ||
    birthdate.trim() === '' ||
    (isEditing && name === initialRef.current?.name && birthdate === initialRef.current?.birthdate)

  return (
    <Popup
      title={isEditing ? 'Edit Athlete' : 'Add Athlete'}
      onClose={onClose}
      content={
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.container}>
            <ImageSelector defaultImage={'/images/anonymous-user.webp'} onImageSelect={handleImageChange} />
            <div className={styles.inputs}>
              <TextField name="name" label="Name" value={name} onChange={handleChange} required />
              <TextField name="birthdate" type="date" value={birthdate} onChange={handleChange} required />
            </div>
          </div>
          <Button text={isEditing ? 'Save Changes' : 'Add'} type="submit" disabled={disabled} width="100%" height="25px" />
          {state.tag === 'editing' && state.error && <div className={styles.error}>{state.error}</div>}
        </form>
      }
    />
  )
}
