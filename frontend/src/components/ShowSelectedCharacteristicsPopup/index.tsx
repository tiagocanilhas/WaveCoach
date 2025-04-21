import * as React from 'react'

import { Characteristics } from '../../pages/Characterisitcs/index'
import { Popup } from '../Popup'
import { Button } from '../Button'

import styles from './styles.module.css'

type ShowSelectedCharacteristicsPopupProps = {
  data?: Characteristics
  onClose: () => void
  onSuccess?: () => void
}

export function ShowSelectedCharacteristicsPopup({ data, onClose, onSuccess }: ShowSelectedCharacteristicsPopupProps) {

  function handleUpdate() {
    // Handle update logic here
  }

  function handleDelete() {
    // Handle delete logic here
  }

  return (
    <Popup
      title={`Selected Characteristics: ${data.date || 'N/A'}`}
      content={
        <div className={styles.characteristics}>
          <p>
            <strong>Height:</strong> {data.height || 'N/A'}
          </p>
          <p>
            <strong>Weight:</strong> {data.weight || 'N/A'}
          </p>
          <p>
            <strong>Calories:</strong> {data.calories || 'N/A'}
          </p>
          <p>
            <strong>Body Fat:</strong> {data.bodyFat || 'N/A'}
          </p>
          <p>
            <strong>Waist Size:</strong> {data.waistSize || 'N/A'}
          </p>
          <p>
            <strong>Arm Size:</strong> {data.armSize || 'N/A'}
          </p>
          <p>
            <strong>Thigh Size:</strong> {data.thighSize || 'N/A'}
          </p>
          <p>
            <strong>Tricep Fat:</strong> {data.tricepFat || 'N/A'}
          </p>
          <p>
            <strong>Abdomen Fat:</strong> {data.abdomenFat || 'N/A'}
          </p>
          <p>
            <strong>Thigh Fat:</strong> {data.thighFat || 'N/A'}
          </p>
          <div className={styles.actions}>
            <Button text="Update" onClick={handleUpdate} width="100%" height="25px" />
            <Button text="Delete" onClick={handleDelete} width="100%" height="25px" />
          </div>
        </div>
      }
      onClose={onClose}
    />
  )
}
