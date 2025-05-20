import * as React from 'react'

import styles from './styles.module.css'

type CardProps = {
  content: React.ReactNode
  width?: string
  height?: string
  onClick?: () => void
}

export function Card({ content, width = null, height = null, onClick }: CardProps) {
  return (
    <div className={styles.card} style={{ width, height }} onClick={onClick}>
      {content}
    </div>
  )
}
