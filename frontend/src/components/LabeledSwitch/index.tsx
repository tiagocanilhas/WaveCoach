import * as React from 'react'

import { Switch } from '@mui/material'

import styles from './styles.module.css'

type LabeledSwitchProps = {
  leftLabel?: string
  rightLabel?: string
  success: boolean
  toggleSuccess: () => void
}

export function LabeledSwitch({ leftLabel, rightLabel, success, toggleSuccess }: LabeledSwitchProps) {
  return (
    <div className={styles.switch}>
      {leftLabel && <span className={styles.label}>{leftLabel}</span>}
      <Switch checked={success} onChange={toggleSuccess} />
      {rightLabel && <span className={styles.label}>{rightLabel}</span>}
    </div>
  )
}
