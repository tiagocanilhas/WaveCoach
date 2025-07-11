import { Wave } from './Wave'

export type WaterWorkout = {
  id: number
  rpe: number
  trimp: number
  condition: string
  time: number
  waves: Wave[]
}
