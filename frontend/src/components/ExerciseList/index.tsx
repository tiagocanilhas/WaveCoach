import * as React from 'react'

import { ObjectList } from '../ObjectList'

import { Exercise } from '../../types/Exercise'

import styles from './styles.module.css'
import { ScrollableText } from '../ScrollableText'

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
        onItemClick={onExerciseClick}
        renderItem={exercise => (
          <div className={styles.exercise}>
            <img src={exercise.url || `/images/no_image.svg`} alt="Exercise" />
            <ScrollableText text={exercise.name} className={styles.exerciseName} />
          </div>
        )}
      />
    </div>
  )
}
