import * as React from 'react'

import { AthletePopup } from '../AthletePopup'
import { Athlete } from '../../types/Athlete'

type EditAthletePopupProps = {
  onClose: () => void
  onSuccess: () => void
  data?: Athlete
}

export function EditAthletePopup({ onClose, onSuccess, data }: EditAthletePopupProps) {
  return <AthletePopup onClose={onClose} onSuccess={onSuccess} data={data} />
}
