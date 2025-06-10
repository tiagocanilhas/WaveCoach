import * as React from 'react'
import { Reorder } from 'framer-motion'

import { Card } from '../Card'

import styles from './styles.module.css'

type VerticalReorderableListProps<T> = {
  list: T[]
  renderItem: (item: T) => React.ReactNode
  onReorder: (newList: T[]) => void
  onClick?: (item: T) => void
  onDelete?: (item: T) => void
  onAdd?: () => void
}

export function VerticalReorderableList<T>({
  list,
  renderItem,
  onReorder,
  onClick,
  onDelete,
  onAdd,
}: VerticalReorderableListProps<T>) {
  return (
    <Reorder.Group axis="y" values={list} onReorder={onReorder} className={styles.list}>
      {list.map(item => (
        <Reorder.Item
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
          width="600px"
        />
      )}
    </Reorder.Group>
  )
}
