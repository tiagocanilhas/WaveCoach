import { Maneuver } from './Maneuver'

export type WaterWorkoutWave = {
  id: number
  points?: number
  order?: number
  rightSide: boolean
  maneuvers: Maneuver[]
}
