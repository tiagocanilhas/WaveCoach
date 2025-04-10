import * as React from 'react'

import styles from './styles.module.css'

type CardProps = {
  content: React.ReactNode
  width?: string
  height?: string
}

export function Card({ content, width = null, height = null }: CardProps) {
  return (
    <div className={styles.card} style={{ width, height }}>
      {content}
    </div>
  )
}
