import * as React from 'react'

import { AthletePopup } from '../AthletePopup'

type EditAthletePopupProps = {
  open: boolean
  onClose: () => void
  onSuccess: () => void
  initialValues?: {
    id: number
    name: string
    birthdate: string
  }
}

export function EditAthletePopup({ open, onClose, onSuccess, initialValues }: EditAthletePopupProps) {
  return <AthletePopup open={open} onClose={onClose} onSuccess={onSuccess} initialValues={initialValues} />
}
