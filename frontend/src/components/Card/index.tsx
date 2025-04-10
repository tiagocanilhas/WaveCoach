import * as React from 'react'

import styles from './styles.module.css'

type CardProps = {
  content: React.ReactNode
}

export function Card({ content }: CardProps) {
  return <div className={styles.card}>{content}</div>
}
