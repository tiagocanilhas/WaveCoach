import * as React from 'react'

import { TextField } from '@mui/material'
import { Button } from '../Button'
import { Popup } from '../Popup'

import { handleError } from '../../../../utils/handleError'

import { createWaterManeuver } from '../../../../services/waterManeuverServices'

import styles from './styles.module.css'
import { useReducer } from 'react'
import { ImageSelector } from '../ImageSelector'

type State =
  | { tag: 'editing'; inputs: { name: string }; image: File; error?: string }
  | { tag: 'submitting'; name: string; image: File | null }
  | { tag: 'submitted' }

type Action =
  | { type: 'edit'; name: string; value: string }
  | { type: 'setImage'; file: File }
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
          return { ...state, image: action.file }
        case 'submit':
          return { tag: 'submitting', name: state.inputs.name, image: state.image }
        default:
          return state
      }

    case 'submitting':
      switch (action.type) {
        case 'error':
          return {
            tag: 'editing',
            error: action.error,
            inputs: { name: state.name },
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
        inputs: { name: '' },
        image: null,
        error: undefined,
      }
  }
}

type AddNewManeuverProps = {
  onClose: () => void
  onSuccess: () => void
}

export function AddNewManeuver({ onClose, onSuccess }: AddNewManeuverProps) {
  const initialState: State = { tag: 'editing', inputs: { name: '' }, image: null, error: undefined }
  const [state, dispatch] = useReducer(reducer, initialState)

  function handleOnChange(e: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', name: e.target.name, value: e.target.value })
  }

  function handleImageSelect(file: File) {
    dispatch({ type: 'setImage', file })
  }

  async function handleOnSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    if (state.tag !== 'editing') return

    const name = state.inputs.name
    const file = state.image

    dispatch({ type: 'submit' })
    try {
      await createWaterManeuver(name, file)
      onSuccess()
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error.res) })
    }
  }

  const exerciseName = state.tag === 'editing' ? state.inputs.name : ''
  const error = state.tag === 'editing' ? state.error : undefined
  const disabled = exerciseName.length === 0

  return (
    <Popup
      title="Add New Maneuver"
      content={
        <>
          <form onSubmit={handleOnSubmit}>
            <div className={styles.container}>
              <ImageSelector defaultImage="/images/no_image.svg" onImageSelect={handleImageSelect} />
              <TextField label="Maneuver Name" name="name" value={exerciseName} onChange={handleOnChange} />
            </div>
            <Button text="Add" type="submit" disabled={disabled} width="100%" height="30px" />
          </form>
          {error && <p className={styles.error}>{error}</p>}
        </>
      }
      onClose={onClose}
    />
  )
}
