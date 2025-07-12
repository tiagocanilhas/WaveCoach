import * as React from 'react'
import { useReducer } from 'react'
import { Competition } from '../../types/Competition'
import { Heat } from '../../types/Heat'

import { HandleCompetitionPopup } from '../HandleCompetitionPopup'

import { diffListOrNull } from '../../../../utils/diffListOrNull'
import { WorkoutEditing } from '../../../../utils/WorkoutEditing'
import { epochConverter } from '../../../../utils/epochConverter'

import { WaterWorkout } from '../../types/WaterWorkout'

import { updateCompetition } from '../../../../services/athleteServices'

import styles from './styles.module.css'

type EditCompetitionPopupProps = {
  competition: Competition
  onClose: () => void
  onSuccess: () => void
}

export function EditCompetitionPopup({ competition, onClose, onSuccess }: EditCompetitionPopupProps) {
  async function handleOnSave(newCompetition: Competition) {
    const name = newCompetition.name === competition.name ? null : newCompetition.name
    const date = newCompetition.date === competition.date ? null : newCompetition.date
    const location = newCompetition.location === competition.location ? null : newCompetition.location
    const place = newCompetition.place === competition.place ? null : newCompetition.place

    const heats = diffListOrNull(newCompetition.heats, (heat, index) => {
      const originalHeat = competition.heats.find(h => h.id === heat.id)

      const score = heat.score === originalHeat.score ? null : heat.score
      const waterActivity = checkWaterActivity(heat.waterActivity, originalHeat.waterActivity)

      const newHeat: Heat = {
        id: heat.id,
        score,
        waterActivity,
      }

      return WorkoutEditing.noEditingMade(newHeat) ? null : newHeat
    })

    await updateCompetition(competition.uid, competition.id, location,  epochConverter(date, 'yyyy-mm-dd'), place, name, heats)
    onSuccess()
  }

  return <HandleCompetitionPopup competition={competition} onSave={handleOnSave} onClose={onClose} />
}

function checkWaterActivity(newWaterActivity: WaterWorkout, oldWaterActivity?: WaterWorkout) {
  const date = newWaterActivity.date === oldWaterActivity.date ? null : newWaterActivity.date
  const condition = newWaterActivity.condition === oldWaterActivity.condition ? null : newWaterActivity.condition
  const rpe = newWaterActivity.rpe === oldWaterActivity.rpe ? null : newWaterActivity.rpe
  const duration = newWaterActivity.duration === oldWaterActivity.duration ? null : newWaterActivity.duration
  const trimp = newWaterActivity.trimp === oldWaterActivity.trimp ? null : newWaterActivity.trimp

  const waves = diffListOrNull(newWaterActivity.waves, (wave, index) => {
    const original = oldWaterActivity.waves.find(w => w.id === wave.id)

    const newWave = {
      id: wave.id,
      points: WorkoutEditing.onlyIfDifferent('points', wave, original || {}),
      rightSide: WorkoutEditing.onlyIfDifferent('rightSide', wave, original || {}),
      maneuvers: diffListOrNull(wave.maneuvers, (maneuver, mIndex) => {

        const originalManeuver = original?.maneuvers.find(m => m.id === maneuver.id)

        const newManeuver = {
          id: maneuver.id,
          waterManeuverId: WorkoutEditing.onlyIfDifferent('waterManeuverId', maneuver, originalManeuver || {}),
          name: WorkoutEditing.onlyIfDifferent('name', maneuver, originalManeuver || {}),
          success: WorkoutEditing.onlyIfDifferent('success', maneuver, originalManeuver || {}),
          order: WorkoutEditing.checkOrder(mIndex, maneuver.order)
        }

        return WorkoutEditing.noEditingMade(newManeuver) ? null : newManeuver
      }),
      order: WorkoutEditing.checkOrder(index, wave.order),
    }


    return WorkoutEditing.noEditingMade(newWave) ? null : newWave
  })

  const water: WaterWorkout = {
    id: oldWaterActivity ? oldWaterActivity.id : null,
    athleteId: null,
    date,
    condition,
    rpe,
    duration,
    trimp,
    waves,
  }

  return WorkoutEditing.noEditingMade(water) ? null : water
}
