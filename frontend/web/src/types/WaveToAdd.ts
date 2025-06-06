import { ManeuverToAdd } from './ManeuverToAdd'

export type WaveToAdd = {
  tempId: string
  rightSide: boolean
  maneuvers: ManeuverToAdd[]
}
