import * as React from 'react'
import { Reorder } from 'framer-motion'

import { Card } from '../Card'

import styles from './styles.module.css'

type ReorderableListProps<T> = {
  list: T[]
  renderItem: (item: T) => React.ReactNode
  onReorder: (newList: T[]) => void
  onClick?: (item: T) => void
  onDelete?: (item: T) => void
  onAdd?: () => void
  cardSize?: string
  order?: 'x' | 'y'
}

export function ReorderableList<T>({
  list,
  renderItem,
  onReorder,
  onClick,
  onDelete,
  onAdd,
  cardSize = '600px',
  order = 'y',
}: ReorderableListProps<T>) {
  return (
    <Reorder.Group
      axis={order}
      values={list}
      onReorder={onReorder}
      className={styles.list}
      style={{ flexDirection: order === 'y' ? 'column' : 'row' }}
    >
      {list.map(item => (
        <Reorder.Item
          className={styles.item}
          key={JSON.stringify(item)}
          value={item}
          whileDrag={{
            opacity: 0.6,
            pointerEvents: 'none',
          }}
        >
          <Card content={renderItem(item)} onClick={() => onClick?.(item)} onDelete={() => onDelete?.(item)} width="100%" />
        </Reorder.Item>
      ))}
      {onAdd && (
        <Card
          content={
            <div className={styles.add} onClick={onAdd}>
              +
            </div>
          }
          width={cardSize}
        />
      )}
    </Reorder.Group>
  )
}
