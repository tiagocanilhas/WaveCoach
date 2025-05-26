import { Maneuver } from './Maneuver'

export type WaterWorkout = {
  id: number
  athleteId: number
  date: number
  pse: number
  condition: string
  heartRate: number
  duration: number
  waves: {
    id: number
    points?: number
    order: number
    maneuvers: Maneuver[]
  }[]
}
