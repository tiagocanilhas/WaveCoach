export type Maneuver = {
  id: number
  tempId?: number
  waterManeuverId: number | null
  name: string
  url?: string
  success: boolean
  order?: number
}
