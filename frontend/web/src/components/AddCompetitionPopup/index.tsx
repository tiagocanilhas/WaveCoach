import * as React from 'react'
import { useReducer } from 'react'
import { Competition } from '../../types/Competition'
import { Heat } from '../../types/Heat'

import { HandleCompetitionPopup } from '../HandleCompetitionPopup'

import { diffListOrNull } from '../../../../utils/diffListOrNull'
import { WorkoutEditing } from '../../../../utils/WorkoutEditing'
import { epochConverter } from '../../../../utils/epochConverter'

import { WaterWorkout } from '../../types/WaterWorkout'

import { createCompetition } from '../../../../services/athleteServices'

import styles from './styles.module.css'

type AddCompetitionPopupProps = {
  onClose: () => void
  onSuccess: () => void
}

export function AddCompetitionPopup({ onClose, onSuccess }: AddCompetitionPopupProps) {
  async function handleOnSave(newCompetition: Competition) {

    console.log('New competition:', newCompetition)

    const location = newCompetition.location
    const date = epochConverter(newCompetition.date, 'yyyy-mm-dd')
    const place = newCompetition.place
    const name = newCompetition.name
    const heats = newCompetition.heats.map(heat => ({
      score: heat.score,
      waterActivity: {
        athleteId: newCompetition.uid,
        rpe: heat.waterActivity.rpe,
        condition: heat.waterActivity.condition,
        trimp: heat.waterActivity.trimp,
        duration: heat.waterActivity.duration,
        waves: heat.waterActivity.waves.map(wave => ({
          points: wave.points,
          rightSide: wave.rightSide,
          maneuvers: wave.maneuvers.map(maneuver => ({
            waterManeuverId: maneuver.waterManeuverId,
            success: maneuver.success,
          })),
        })),
      },
    }))

    await createCompetition(newCompetition.uid, date, location, place, name, heats)
    onSuccess()
  }

  return <HandleCompetitionPopup onSave={handleOnSave} onClose={onClose} />
}
