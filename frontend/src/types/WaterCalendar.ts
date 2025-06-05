import { Maneuver } from './Maneuver'

export type WaterCalendar = {
  mesocycles: {
    id: number
    startTime: number
    endTime: number
    microcycles: {
      id: number
      startTime: number
      endTime: number
      activities: {
        id: number
        athleteId: number
        microcycleId: number
        date: number
        pse: number
        condition: string
        heartRate: number
        duration: number
        waves: {
          id: number
          points: number
          rightSide: boolean
          order: number
          maneuvers: Maneuver[]
        }[]
      }[]
    }[]
  }[]
}
