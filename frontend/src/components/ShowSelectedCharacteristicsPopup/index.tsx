import React from 'react';
import styles from './styles.module.css';
import { Characteristics } from '../../pages/Characterisitcs/index';
import { Popup } from '../Popup';

type ShowSelectedCharacteristicsPopupProps = {
  open: boolean
  onClose: () => void
  onSuccess: () => void
  data?: Characteristics
};

export function ShowSelectedCharacteristicsPopup({
  open,
  onClose,
  onSuccess,
  data,
}: ShowSelectedCharacteristicsPopupProps) {
  if (!data) return null;

  return (
    <Popup
      title={`Selected Characteristics: ${data.date || 'N/A'}`}
      content={
        <div className={styles.characteristics}>
          <p><strong>Height:</strong> {data.height || 'N/A'}</p>
          <p><strong>Weight:</strong> {data.weight || 'N/A'}</p>
          <p><strong>Calories:</strong> {data.calories || 'N/A'}</p>
          <p><strong>Body Fat:</strong> {data.bodyFat || 'N/A'}</p>
          <p><strong>Waist Size:</strong> {data.waistSize || 'N/A'}</p>
          <p><strong>Arm Size:</strong> {data.armSize || 'N/A'}</p>
          <p><strong>Thigh Size:</strong> {data.thighSize || 'N/A'}</p>
          <p><strong>Tricep Fat:</strong> {data.tricepFat || 'N/A'}</p>
          <p><strong>Abdomen Fat:</strong> {data.abdomenFat || 'N/A'}</p>
          <p><strong>Thigh Fat:</strong> {data.thighFat || 'N/A'}</p>
          <div className={styles.actions}>
            <button className={styles.btn} >
              Update
            </button>
            <button className={styles.btn} >
              Delete
            </button>
          </div>
        </div>
      }
      onClose={onClose}
    />
  );
    
}