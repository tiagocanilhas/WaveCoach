import { StringifyOptions } from 'querystring'

export type Maneuver = {
  id: number
  waterManeuverId: number
  name: string
  url?: string
  success: boolean
  order: number
}
