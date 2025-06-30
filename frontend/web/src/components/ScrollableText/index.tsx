import * as React from 'react'
import { useRef, useState } from 'react'

import Marquee from 'react-fast-marquee'

import styles from './styles.module.css'

type ScrollableTextProps = {
  text: string
  className?: string
}

export function ScrollableText({ text, className }: ScrollableTextProps) {
  const [isHovered, setIsHovered] = useState(false)
  const textRef = useRef<HTMLSpanElement>(null)

  function handleHover() {
    if (textRef.current) {
      const textWidth = textRef.current.offsetWidth
      const containerWidth = textRef.current.parentElement?.offsetWidth || 0
      setIsHovered(textWidth > containerWidth)
    }
  }

  return (
    <div
      onMouseEnter={handleHover}
      onMouseLeave={() => setIsHovered(false)}
      className={styles.container}
      data-testid="scrollable-text"
    >
      {isHovered ? (
        <Marquee speed={40} gradient={false}>
          <span style={{ paddingRight: '50px' }} className={className}>
            {text}
          </span>
        </Marquee>
      ) : (
        <span ref={textRef} className={className}>
          {text}
        </span>
      )}
    </div>
  )
}
