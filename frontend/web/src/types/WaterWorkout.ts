import { WaterWorkoutWave } from './WaterWorkoutWave'

export type WaterWorkout = {
  id: number
  athleteId: number
  date: number
  rpe: number
  condition: string
  trimp: number
  duration: number
  waves: WaterWorkoutWave[]
}
