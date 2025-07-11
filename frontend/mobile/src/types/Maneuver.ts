export type Maneuver = {
  id: number | null
  tempId?: number
  waterManeuverId: number
  name: string
  url?: string
  success: boolean
  order?: number
}
