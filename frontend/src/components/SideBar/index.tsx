import * as React from 'react'
import { ReactNode } from 'react'
import { FaUser } from 'react-icons/fa'
import { RiLogoutBoxRFill } from 'react-icons/ri'
import { Link } from 'react-router-dom'

import styles from './styles.module.css'

type Item = {
  title: string
  path: string
  icon?: ReactNode
}

type SideBarProps = {
  isOpen: boolean
  sidebarData: Item[]
}

export function SideBar({ isOpen, sidebarData }: SideBarProps) {
  return (
    <div className={`${styles.sidebar} ${isOpen ? styles.sidebarShown : styles.sidebarHidden}`}>
      <ul className={styles.sidebarItems}>
        {sidebarData.map((item, index) => {
          return (
            <li key={index} className={styles.sidebarText}>
              <Link to={item.path}>
                {item.icon}
                <span>{item.title}</span>
              </Link>
            </li>
          )
        })}
      </ul>
      <ul className={styles.sidebarItems}>
        <li className={styles.sidebarBottomText}>
          <Link to="/athletes">
            <span>Athletes</span>
          </Link>
        </li>
        <li className={styles.sidebarBottomText}>
          <Link to="/account">
            <FaUser />
            <span>Account</span>
          </Link>
        </li>
        <li className={styles.sidebarBottomText}>
          <Link to="/logout">
            <RiLogoutBoxRFill />
            <span>Logout</span>
          </Link>
        </li>
      </ul>
    </div>
  )
}
