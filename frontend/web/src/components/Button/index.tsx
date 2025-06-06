import * as React from 'react'
import { useLayoutEffect, useRef, useState } from 'react'

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
  const ref = useRef<HTMLButtonElement>(null)
  const [fontSize, setFontSize] = useState(16)

  useLayoutEffect(() => {
    function resizeFont() {
      if (ref.current) {
        const height = ref.current.offsetHeight
        setFontSize(height * 0.6)
      }
    }

    resizeFont()
    const observer = new ResizeObserver(resizeFont)
    if (ref.current) observer.observe(ref.current)

    return () => observer.disconnect()
  }, [])

  return (
    <button
      className={styles.btn}
      onClick={onClick}
      disabled={disabled}
      type={type}
      style={{ width: width, height: height, fontSize: `${fontSize}px` }}
      ref={ref}
    >
      {text}
    </button>
  )
}
