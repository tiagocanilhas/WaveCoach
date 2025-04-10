import * as React from 'react'
import { useEffect, useRef, useState } from 'react'

import styles from './styles.module.css'

type DropdownProps = {
  options: string[]
}

export function Dropdown({ options }: DropdownProps) {
  const [isOpen, setIsOpen] = useState(false)
  const dropdownRef = useRef<HTMLDivElement | null>(null)

  function toggle() {
    setIsOpen(prev => !prev)
  }

  function handleClickOutside(ev: MouseEvent) {
    if (dropdownRef.current && !dropdownRef.current.contains(ev.target as Node)) setIsOpen(false)
  }

  useEffect(() => {
    document.addEventListener('mousedown', handleClickOutside)

    return () => {
      document.removeEventListener('mousedown', handleClickOutside)
    }
  }, [])

  return (
    <div className={styles.dropdown} ref={dropdownRef}>
      <div onClick={toggle} className={styles.dropdownButton}>
        ⋮
      </div>
      {isOpen && (
        <ul className={styles.dropdownMenu}>
          {options.map((option, index) => (
            <li key={index} className={styles.dropdownItem}>
              {option}
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
