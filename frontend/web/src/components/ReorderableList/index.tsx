import * as React from 'react'
import { FaBars } from 'react-icons/fa'
import { Reorder, useDragControls } from 'framer-motion'
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
        <ReorderableListItem
          key={JSON.stringify(item)}
          item={item}
          renderItem={renderItem}
          onClick={onClick}
          onDelete={onDelete}
        />
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

type ReorderableListItemProps<T> = {
  item: T
  renderItem: (item: T) => React.ReactNode
  onClick?: (item: T) => void
  onDelete?: (item: T) => void
}

function ReorderableListItem<T>({ item, renderItem, onClick, onDelete }: ReorderableListItemProps<T>) {
  const controls = useDragControls()

  return (
    <Reorder.Item
      className={styles.item}
      value={item}
      dragListener={false}
      dragControls={controls}
      whileDrag={{ opacity: 0.6, pointerEvents: 'none' }}
    >
      <Card
        content={
          <div className={styles.item}>
            <FaBars className={styles.dragItem} onPointerDown={e => controls.start(e)} />
            {renderItem(item)}
          </div>
        }
        onClick={() => onClick?.(item)}
        onDelete={() => onDelete?.(item)}
        width="100%"
      />
    </Reorder.Item>
  )
}
