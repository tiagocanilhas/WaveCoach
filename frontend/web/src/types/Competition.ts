import { Heat } from './Heat'

export type Competition = {
  id: number
  uid: number
  name: string
  date: number
  location: string
  place: number
  heats: Heat[]
}
