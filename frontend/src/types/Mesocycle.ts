import { Microcycle } from './Microcycle'

export type Mesocycle = {
  id: number
  startTime: number
  endTime: number
  microcycles: Microcycle[]
}
