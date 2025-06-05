import * as React from 'react'

import { Switch } from '@mui/material'

import styles from './styles.module.css'

type LabeledSwitchProps = {
  leftLabel?: string
  rightLabel?: string
  checked: boolean
  onChange: () => void
}

export function LabeledSwitch({ leftLabel, rightLabel, checked, onChange }: LabeledSwitchProps) {
  return (
    <div className={styles.switch}>
      {leftLabel && <span className={styles.label}>{leftLabel}</span>}
      <Switch checked={checked} onChange={onChange} />
      {rightLabel && <span className={styles.label}>{rightLabel}</span>}
    </div>
  )
}
