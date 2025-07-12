import * as React from 'react'

import styles from './styles.module.css'

type CardProps = {
  content: React.ReactNode
  width?: string
  height?: string
  onClick?: () => void
  onDelete?: () => void
}

export function Card({ content, width = null, height = null, onClick, onDelete }: CardProps) {
  return (
    <div
      className={styles.card}
      style={{ width, height }}
      onClick={e => {
        e.stopPropagation()
        onClick && onClick()
      }}
    >
      {onDelete && (
        <div
          className={styles.delete}
          onClick={e => {
            e.stopPropagation()
            onDelete()
          }}
        >
          X
        </div>
      )}
      {content}
    </div>
  )
}
