import * as React from 'react'
import { Key, ReactNode } from 'react'

import { Card } from '../Card'

import styles from './styles.module.css'

type ObjectListProps<T> = {
  items: T[]
  getKey: (item: T) => Key
  renderItem: (item: T) => ReactNode
  onAdd?: () => void
  onItemClick?: (item: T) => void
}

export function ObjectList<T>({ items, renderItem, onAdd, getKey, onItemClick }: ObjectListProps<T>) {
  const size = '200px'
  return (
    <div className={styles.container}>
      {onAdd && (
        <Card
          content={
            <div className={styles.add} onClick={onAdd}>
              +
            </div>
          }
          width={size}
          height={size}
        />
      )}

      {items.map(item => (
        <Card
          key={getKey(item)}
          content={renderItem(item)}
          width={size}
          height={size}
          onClick={() => onItemClick && onItemClick(item)}
        />
      ))}
    </div>
  )
}
