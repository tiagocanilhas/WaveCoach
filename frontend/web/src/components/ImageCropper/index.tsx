import * as React from 'react'
import Cropper from 'react-easy-crop'
import { useReducer } from 'react'

import styles from './styles.module.css'
import { Button } from '../Button'
import { Popup } from '../Popup'

type State = {
  crop: { x: number; y: number }
  zoom: number
  croppedAreaPixels: any
  cropping: boolean
}

type Action =
  | { type: 'setCrop'; crop: { x: number; y: number } }
  | { type: 'setZoom'; zoom: number }
  | { type: 'setCroppedAreaPixels'; croppedAreaPixels: any }
  | { type: 'setCropping'; cropping: boolean }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'setCrop':
      return { ...state, crop: action.crop }
    case 'setZoom':
      return { ...state, zoom: action.zoom }
    case 'setCroppedAreaPixels':
      return { ...state, croppedAreaPixels: action.croppedAreaPixels }
    case 'setCropping':
      return { ...state, cropping: action.cropping }
    default:
      return state
  }
}

type ImageCropperProps = {
  image: string
  onCancel: () => void
  onComplete: (file: File) => void
}

export function ImageCropper({ image, onCancel, onComplete }: ImageCropperProps) {
  const initialState: State = {
    crop: { x: 0, y: 0 },
    zoom: 1,
    croppedAreaPixels: null,
    cropping: false,
  }

  const [state, dispatch] = useReducer(reducer, initialState)

  function onCropComplete(_: any, croppedAreaPixels: any) {
    dispatch({ type: 'setCroppedAreaPixels', croppedAreaPixels })
  }

  function handleSetCrop(crop: { x: number; y: number }) {
    dispatch({ type: 'setCrop', crop })
  }

  function handleZoomChange(zoom: number) {
    dispatch({ type: 'setZoom', zoom })
  }

  async function handleCrop() {
    dispatch({ type: 'setCropping', cropping: true })
    try {
      const file = await getCroppedImg(image, state.croppedAreaPixels)
      if (file) onComplete(file)
    } catch (error) {
      console.error('Erro ao recortar a imagem:', error)
    }
    dispatch({ type: 'setCropping', cropping: false })
  }

  const crop = state.crop
  const zoom = state.zoom
  const cropping = state.cropping

  return (
    <Popup
      content={
        <div className={styles.container}>
          <div className={styles.cropper}>
            <Cropper
              image={image}
              crop={crop}
              zoom={zoom}
              aspect={1}
              onCropChange={handleSetCrop}
              onZoomChange={handleZoomChange}
              onCropComplete={onCropComplete}
            />
          </div>
          <Button onClick={handleCrop} disabled={cropping} text="Confirm" width="100%" height="30px" />
        </div>
      }
      onClose={onCancel}
    />
  )
}

async function getCroppedImg(imageSrc: string, crop: any): Promise<File | null> {
  const image = new Image()
  image.src = imageSrc
  await new Promise(resolve => (image.onload = resolve))

  const canvas = document.createElement('canvas')
  canvas.width = crop.width
  canvas.height = crop.height

  const ctx = canvas.getContext('2d')!
  ctx.drawImage(image, crop.x, crop.y, crop.width, crop.height, 0, 0, crop.width, crop.height)

  return new Promise(resolve => {
    canvas.toBlob(blob => {
      if (!blob) return resolve(null)
      const file = new File([blob], 'cropped-image.png', { type: 'image/png' })
      resolve(file)
    }, 'image/png')
  })
}
