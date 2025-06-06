import * as React from 'react'

import { FaBars } from 'react-icons/fa'

import styles from './styles.module.css'
import { AiOutlineClose } from 'react-icons/ai'

type TopBarProps = {
  title?: string
  isOpen: boolean
  handleShowSidebar: () => void
}

export function TopBar({ title, isOpen, handleShowSidebar }: TopBarProps) {
  return (
    <div className={styles.topbar}>
      {isOpen ? (
        <AiOutlineClose className={styles.menuBars} onClick={handleShowSidebar} />
      ) : (
        <FaBars className={styles.menuBars} onClick={handleShowSidebar} />
      )}
      <h1 className={styles.title}>{title}</h1>
    </div>
  )
}
