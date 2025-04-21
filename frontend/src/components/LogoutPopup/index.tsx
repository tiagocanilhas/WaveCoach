import * as React from 'react'

import { Popup } from '../Popup'
import { Button } from '../Button'

import styles from './styles.module.css'

type LogoutPopupProps = {
  onLogout: () => void
  onCancel: () => void
}

export function LogoutPopup({ onLogout, onCancel }: LogoutPopupProps) {
  return (
    <Popup
      title="Logout"
      content={
        <div className={styles.container}>
          <p>Are you sure you want to logout?</p>
          <Button text="Logout" onClick={onLogout} width="50%" height="25px" />
        </div>
      }
      onClose={onCancel}
    />
  )
}
