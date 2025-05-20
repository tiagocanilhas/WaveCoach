import { Exercise } from "./Exercise"

export type GymWorkout = {
    id: string
    date: number
    exercises: {
        id: string
        name: string
        order: number
        sets: {
            id: string
            reps: number
            weight: number
            restTime: number
            order: number
        }[]
    }[]
}