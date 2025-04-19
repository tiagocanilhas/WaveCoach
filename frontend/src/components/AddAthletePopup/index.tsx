import * as React from 'react'

import { AthletePopup } from '../AthletePopup'

type AddAthletePopupProps = {
  open: boolean
  onClose: () => void
  onSuccess: () => void
}

export function AddAthletePopup({ open, onClose, onSuccess }: AddAthletePopupProps) {
  return <AthletePopup open={open} onClose={onClose} onSuccess={onSuccess} />
}
