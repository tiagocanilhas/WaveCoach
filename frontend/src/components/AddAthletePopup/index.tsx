import * as React from 'react'

import { AthletePopup } from '../AthletePopup'

type AddAthletePopupProps = {
  onClose: () => void
  onSuccess: () => void
}

export function AddAthletePopup({ onClose, onSuccess }: AddAthletePopupProps) {
  return <AthletePopup onClose={onClose} onSuccess={onSuccess} />
}
