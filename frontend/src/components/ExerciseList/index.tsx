import * as React from 'react'

import { ObjectList } from '../ObjectList'

import { Exercise } from '../../types/Exercise'

import styles from './styles.module.css'

type ExerciseListProps = {
  items: Exercise[]
  category: string
  onAdd: (type: string) => void
  onExerciseClick: (exercise: Exercise) => void
}

export function ExerciseList({ items, category, onAdd, onExerciseClick }: ExerciseListProps) {
  const filteredItems = items.filter(exercise => exercise.category === category.toLowerCase())
  return (
    <div className={styles.exerciseList}>
      <h2>{category}</h2>
      <ObjectList
        items={filteredItems}
        getKey={exercise => exercise.id}
        onAdd={() => onAdd(category.toLowerCase())}
        renderItem={exercise => (
          <div className={styles.exercise}>
            <img src={`/images/no_image.svg`} alt="Exercise" onClick={() => onExerciseClick(exercise)} />
            <h3>{exercise.name}</h3>
          </div>
        )}
      />
    </div>
  )
}
