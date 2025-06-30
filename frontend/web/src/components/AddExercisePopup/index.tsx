import * as React from 'react'
import { useState } from 'react'

import { Popup } from '../Popup'
import { Button } from '../Button'
import { TextField } from '@mui/material'
import { Card } from '../Card'

import { Exercise } from '../../types/Exercise'
import { SetDataToAdd } from '../../types/SetDataToAdd'

import styles from './styles.module.css'

type AddExercisePopupProps = {
  data?: { sets: SetDataToAdd[] }
  exercise: Exercise
  onAdd: (exercise: Exercise, sets: SetDataToAdd[]) => void
  onClose: () => void
}

export function AddExercisePopup({ data, exercise, onAdd, onClose }: AddExercisePopupProps) {
  const [sets, setSets] = useState<SetDataToAdd[]>(data?.sets || [{ reps: undefined, weight: undefined, restTime: undefined }])

  function handleAddSet() {
    setSets(prev => [...prev, { reps: undefined, weight: undefined, restTime: undefined }])
  }

  function handleRemoveSet(index: number) {
    setSets(prev => prev.filter((_, i) => i !== index))
  }

  function handleChangeInteger(index: number, field: keyof SetDataToAdd, value: string) {
    if (/^\d*$/.test(value)) {
      const parsed = value === '' ? undefined : parseInt(value, 10)
      setSets(prev => {
        const newSets = [...prev]
        newSets[index][field] = parsed
        return newSets
      })
    }
  }

  function handleChangeFloat(index: number, field: keyof SetDataToAdd, value: string) {
    const sanitized = value.replace(',', '.')
    if (/^\d*\.?\d*$/.test(sanitized)) {
      const parsed = sanitized === '' ? undefined : parseFloat(sanitized)
      setSets(prev => {
        const newSets = [...prev]
        newSets[index][field] = parsed
        return newSets
      })
    }
  }

  function handleAddExercise() {
    onAdd(exercise, sets)
    onClose()
  }

  const disabled = sets.some(set => set.reps === undefined || set.weight === undefined || set.restTime === undefined)

  return (
    <Popup
      title="Add Exercise"
      content={
        <>
          <div className={styles.exercise}>
            <img src={exercise.url || `/images/no_image.svg`} alt="Exercise" />
            <h2>{exercise.name}</h2>
          </div>

          <div className={styles.setsContainer}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Set</th>
                  <th>Reps</th>
                  <th>Weight (kg)</th>
                  <th>Rest (s)</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {sets.map((set, index) => (
                  <tr key={index}>
                    <td>{index + 1}</td>
                    <td>
                      <TextField
                        value={set.reps ?? ''}
                        onChange={e => handleChangeInteger(index, 'reps', e.target.value)}
                        inputMode="numeric"
                        type="number"
                      />
                    </td>
                    <td>
                      <TextField
                        value={set.weight ?? ''}
                        onChange={e => handleChangeFloat(index, 'weight', e.target.value)}
                        inputMode="numeric"
                        type="number"
                        inputProps={{ step: '0.1' }}
                      />
                    </td>
                    <td>
                      <TextField
                        value={set.restTime ?? ''}
                        onChange={e => handleChangeInteger(index, 'restTime', e.target.value)}
                        inputMode="numeric"
                        type="number"
                      />
                    </td>
                    <td>
                      <div onClick={() => handleRemoveSet(index)} className={styles.remove}>
                        🗑️
                      </div>
                    </td>
                  </tr>
                ))}
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center' }}>
                    <Card
                      content={
                        <div className={styles.add} onClick={handleAddSet}>
                          +
                        </div>
                      }
                      width="100%"
                    />
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <Button text="Add" disabled={disabled} onClick={handleAddExercise} width="100%" height="30px" />
        </>
      }
      onClose={onClose}
    />
  )
}
