import { Maneuver } from './Maneuver'

export type Wave = {
  maneuvers: {
    maneuver: Maneuver
    isRight: boolean
    success: boolean
  }[]
}
