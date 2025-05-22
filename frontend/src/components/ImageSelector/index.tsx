import * as React from 'react'
import { useReducer, useRef } from 'react'
import { ImageCropper } from '../ImageCropper'

import styles from './styles.module.css'

type State = {
  file?: File
  previewUrl: string
  rawUrl?: string
  isCropping: boolean
}

type Action =
  | { type: 'selectRaw'; rawUrl: string }
  | { type: 'cropComplete'; file: File; previewUrl: string }
  | { type: 'cancel' }
  | { type: 'reset'; defaultUrl: string }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'selectRaw':
      return { ...state, rawUrl: action.rawUrl, isCropping: true }
    case 'cropComplete':
      return { ...state, file: action.file, previewUrl: action.previewUrl, rawUrl: null, isCropping: false }
    case 'cancel':
      return { ...state, rawUrl: undefined, isCropping: false }
    case 'reset':
      return { file: undefined, previewUrl: action.defaultUrl, rawUrl: null, isCropping: false }
    default:
      return state
  }
}

type ImageSelectorProps = {
  defaultImage: string
  onImageSelect: (file: File) => void
}

export function ImageSelector({ defaultImage, onImageSelect }: ImageSelectorProps) {
  const fileInputRef = useRef<HTMLInputElement>(null)

  const initialState: State = { file: undefined, previewUrl: defaultImage, rawUrl: null, isCropping: false }
  const [state, dispatch] = useReducer(reducer, initialState)

  function handleClick() {
    fileInputRef.current?.click()
  }

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0]
    if (!file) return

    const rawUrl = URL.createObjectURL(file)
    dispatch({ type: 'selectRaw', rawUrl })
  }

  function handleCropComplete(file: File) {
    const previewUrl = URL.createObjectURL(file)
    dispatch({ type: 'cropComplete', file, previewUrl })
    onImageSelect(file)
  }

  function handleCancel() {
    dispatch({ type: 'cancel' })
  }

  const rawUrl = state.rawUrl
  const isCropping = state.isCropping

  return (
    <>
      {isCropping && rawUrl ? (
        <ImageCropper image={state.rawUrl} onCancel={handleCancel} onComplete={handleCropComplete} />
      ) : (
        <div className={styles.container}>
          <div onClick={handleClick}>
            <img src={state.previewUrl} alt="Preview" />
          </div>
          <input type="file" accept="image/*" ref={fileInputRef} onChange={handleChange} />
        </div>
      )}
    </>
  )
}
