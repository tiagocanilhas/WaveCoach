import * as React from 'react'

import { TextField } from '@mui/material'
import { Card } from '../../components/Card'

import styles from './styles.module.css'

export function Account() {
  return (
    <div className={styles.container}>
      <Card
        content={
          <>
            <h2 className={styles.title}>Account Settings</h2>
            <div className={styles.inputContainer}>
              <TextField label="Username" required />
              <TextField label="Password" required />
              <TextField label="Confirm Password" required />
            </div>
          </>
        }
      />
    </div>
  )
}
