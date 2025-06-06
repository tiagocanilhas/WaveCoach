import * as React from 'react'

import styles from './styles.module.css'

export function BackButton({ onClick }: { onClick: () => void }) {
  return (
    <button className={styles.backButton} onClick={onClick}>
      ◀
    </button>
  )
}
