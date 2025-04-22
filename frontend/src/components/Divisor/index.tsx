import * as React from 'react'
import { ReactNode } from 'react'

import styles from './styles.module.css'

type DivisorProps = {
  left: ReactNode
  right: ReactNode
}

export function Divisor({ left, right }: DivisorProps) {
  return (
    <div className={styles.container}>
      <div className={styles.left}>{left}</div>
      <div className={styles.right}>{right}</div>
    </div>
  )
}
