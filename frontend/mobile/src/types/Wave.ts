import { Maneuver } from './Maneuver'

export type Wave = {
  id: number | null
  tempId?: number
  rightSide: boolean
  maneuvers: Maneuver[]
  order?: number
}
