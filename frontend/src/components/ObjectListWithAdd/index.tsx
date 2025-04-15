import * as React from 'react'
import styles from './styles.module.css'
import { Card } from '../../components/Card'

type ObjectListWithAddProps<T> = {
  items: T[]
  renderItem: (item: T) => React.ReactNode
  onAdd: () => void
}

export function ObjectListWithAdd<T>({ items, renderItem, onAdd }: ObjectListWithAddProps<T>) {
  const size = '200px'
  return (
    <div className={styles.container}>
      <Card
        content={
          <div className={styles.add} onClick={onAdd}>
            +
          </div>
        }
        width={size}
        height={size}
      />

      {items.map((item, idx) => (
        <Card key={idx} content={renderItem(item)} width={size} height={size} />
      ))}
    </div>
  )
}
