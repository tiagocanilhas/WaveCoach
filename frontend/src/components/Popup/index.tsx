import React, { useState } from 'react'

import styles from './styles.module.css'

type PopupProps = {
  title?: string
  content: React.ReactNode
  onClose: () => void
}

export function Popup({ title, content, onClose }: PopupProps) {
  return (
    <div className={styles.overlay}>
      <div className={styles.container}>
        <button onClick={onClose} className={styles.closeButton}>
          X
        </button>
        {title && <div className={styles.title}>{title}</div>}
        <div className={styles.content}>{content}</div>
      </div>
    </div>
  )
}
