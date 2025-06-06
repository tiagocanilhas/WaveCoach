import { Maneuver } from './Maneuver'

export type WaterWorkout = {
  id: number
  athleteId: number
  date: number
  rpe: number
  condition: string
  trimp: number
  duration: number
  waves: {
    id: number
    points?: number
    order: number
    rightSide: boolean
    maneuvers: Maneuver[]
  }[]
}
