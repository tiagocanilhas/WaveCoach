export type GymWorkout = {
  id: string
  date: number
  exercises: {
    id: string
    name: string
    order: number
    url?: string
    sets: {
      id: string
      reps: number
      weight: number
      restTime: number
      order: number
    }[]
  }[]
}
