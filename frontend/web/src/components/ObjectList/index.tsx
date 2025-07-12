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
  cardSize?: string
}

export function ObjectList<T>({ items, renderItem, onAdd, getKey, onItemClick, cardSize = '200px' }: ObjectListProps<T>) {
  return (
    <div className={styles.container} style={{ gridTemplateColumns: `repeat(auto-fill, minmax(${cardSize}, 1fr))` }}>
      {onAdd && (
        <Card
          content={
            <div className={styles.add} onClick={onAdd}>
              +
            </div>
          }
          width={cardSize}
          height={cardSize}
        />
      )}

      {items.map(item => (
        <Card
          key={getKey(item)}
          content={renderItem(item)}
          width={cardSize}
          height={cardSize}
          onClick={() => onItemClick && onItemClick(item)}
        />
      ))}
    </div>
  )
}
