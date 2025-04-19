import * as React from 'react'
import { ReactNode, useState } from 'react'
import { FaUser } from 'react-icons/fa'
import { RiLogoutBoxRFill } from 'react-icons/ri'
import { Link } from 'react-router-dom'

import { LogoutPopup } from '../LogoutPopup'

import { logout } from '../../services/userServices'

import { useAuthentication } from '../../hooks/useAuthentication'

import styles from './styles.module.css'

type Item = {
  title: string
  path: string
  icon?: ReactNode
}

type SideBarProps = {
  isOpen: boolean
  sidebarData: Item[]
  closeSidebar?: () => void
}

export function SideBar({ isOpen, sidebarData, closeSidebar }: SideBarProps) {
  const [logoutPopup, setLogoutPopup] = useState(false)
  const [_, setUser] = useAuthentication()

  async function handleLogout() {
    try {
      await logout()
      setUser(undefined)
    } catch (err) {
      console.error('Logout failed:', err)
    } finally {
      setLogoutPopup(false)
    }
  }

  function handlePopup() {
    setLogoutPopup((prev) => !prev)
  }

  return (
    <>
      {isOpen && <div className={styles.overlay} onClick={closeSidebar} />}

      <div className={`${styles.sidebar} ${isOpen ? styles.sidebarShown : styles.sidebarHidden}`}>
        <ul className={styles.sidebarItems} onClick={closeSidebar}>
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
        <ul className={styles.sidebarItems} onClick={closeSidebar}>
          <li className={styles.sidebarBottomText}>
            <Link to="/">
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
            <div onClick={(e) => { e.preventDefault(); handlePopup() }} className={styles.logout}>
              <RiLogoutBoxRFill />
              <span>Logout</span>
            </div>
          </li>
        </ul>
      </div>

      {logoutPopup && <LogoutPopup onLogout={handleLogout} onCancel={handlePopup} />}
    </>
  )
}
