import * as React from 'react'
import { useState } from 'react'

import { Popup } from '../Popup'
import { TextField } from '@mui/material'
import { Card } from '../Card'
import { Button } from '../Button'

import { GymWorkoutExercise } from '../../types/GymWorkoutExercise'
import { GymWorkoutSet } from '../../types/GymWorkoutSet'

import styles from './styles.module.css'
import { WorkoutEditing } from '../../../../utils/WorkoutEditing'

type EditExercisePopupProps = {
  exercise: GymWorkoutExercise
  onSave:(exercise: GymWorkoutExercise) => void
  onClose: () => void
}

export function EditExercisePopup({ exercise, onSave, onClose }: EditExercisePopupProps) {
  const { editable, removed } = exercise.sets.reduce((acc, set) => {
      if (WorkoutEditing.checkDeleteObject(set)) acc.removed.push(set)
      else acc.editable.push(set)
      return acc
    }, { editable: [] as GymWorkoutSet[], removed: [] as GymWorkoutSet[] }
  )
  const [sets, setSets] = useState<GymWorkoutSet[]>(editable)
  const [removedSets, setRemovedSets] = useState<GymWorkoutSet[]>(removed)

  function handleRemoveSet(index: number) {
    const setToRemove = WorkoutEditing.nullifyFieldsExceptId(sets[index])
    if (setToRemove.id) {
      setRemovedSets(prev => [...prev, setToRemove])
    }
    setSets(prev => prev.filter((_, i) => i !== index))
  }

  function handleAddSet() {
    setSets(prev => [...prev, {
      ...prev[prev.length - 1],
      id: null,
      order: prev.length
    }])
  }

  function handleOnSave(){
    onSave({...exercise, sets: [...sets, ...removedSets] })
    onClose
  }

  function handleChangeInteger(index: number, field: keyof GymWorkoutSet, value: string) {
    if (/^\d*$/.test(value)) {
      const parsed = value === '' ? undefined : parseInt(value, 10)
      setSets(prev => {
        const newSets = [...prev]
        newSets[index][field] = parsed
        return newSets
      })
    }
  }

  function handleChangeFloat(index: number, field: keyof GymWorkoutSet, value: string) {
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

  return (
    <Popup
      title="Edit Exercise"
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

          <Button text="Save" onClick={handleOnSave} width="100%" height="30px" />
        </>
      }
      onClose={onClose}
    />
  )
}
