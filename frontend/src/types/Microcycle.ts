import { Activity } from './Activity'

export type Microcycle = {
  id: number
  startTime: number
  endTime: number
  activities: Activity[]
}
