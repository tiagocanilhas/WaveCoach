export type GymWorkout = {
  id: number
  date: number
  exercises: {
    id: number
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
