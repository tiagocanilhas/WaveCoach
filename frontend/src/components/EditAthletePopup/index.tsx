import * as React from 'react'

import { AthletePopup } from '../AthletePopup'

type EditAthletePopupProps = {
  onClose: () => void
  onSuccess: () => void
  initialValues?: {
    id: number
    name: string
    birthdate: string
  }
}

export function EditAthletePopup({ onClose, onSuccess, initialValues }: EditAthletePopupProps) {
  return <AthletePopup onClose={onClose} onSuccess={onSuccess} initialValues={initialValues} />
}
