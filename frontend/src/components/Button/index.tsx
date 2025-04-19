import * as React from 'react'

import styles from './styles.module.css'

type ButtonProps = {
  text: string
  onClick?: () => void
  disabled?: boolean
  type?: 'button' | 'submit' | 'reset'
  width?: string
  height?: string
}

export function Button({ text, onClick, disabled = false, type = 'button', width, height }: ButtonProps) {
  return (
    <button className={styles.btn} onClick={onClick} disabled={disabled} type={type} style={{ width: width, height }}>
      {text}
    </button>
  )
}
