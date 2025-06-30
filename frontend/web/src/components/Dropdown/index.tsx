import * as React from 'react'
import { useEffect, useRef, useState } from 'react'

import styles from './styles.module.css'

type DropdownOption = {
  label: string
  disabled?: boolean
  onClick: () => void
}

type DropdownProps = {
  options: DropdownOption[]
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
    <div className={styles.dropdown} ref={dropdownRef} data-testid="dropdown">
      <div onClick={toggle} className={styles.dropdownButton}>
        ⋮
      </div>
      {isOpen && (
        <ul className={styles.dropdownMenu}>
          {options.map((option, index) => {
            if (option.disabled) return
            return (
              <li
                key={index}
                className={styles.dropdownItem}
                onClick={() => {
                  option.onClick()
                  setIsOpen(false)
                }}
              >
                {option.label}
              </li>
            )
          })}
        </ul>
      )}
    </div>
  )
}
