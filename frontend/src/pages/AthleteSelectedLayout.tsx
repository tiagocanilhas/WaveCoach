import * as React from 'react'
import { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { Bars } from '../components/Bars'

export function AthleteSelectedLayout() {
  const [athleteName, setAthleteName] = useState('')
  const { aid: id } = useParams<{ aid: string }>()

  useEffect(() => {
    async function fetchAthleteName() {
      try {
        const res = 'name'
        setAthleteName(res)
      } catch (error) {
        setAthleteName('WaveCoach')
      }
    }
    fetchAthleteName()
  }, [])

  const sidebarItems = [
    { title: 'Athlete Profile', path: `athlete/${id}`, cName: 'sidebar-text' },
    { title: 'Characteristics', path: `athlete/${id}/characteristics`, cName: 'sidebar-text' },
    { title: 'Gym Workouts', path: `athlete/${id}/gym`, cName: 'sidebar-text' },
    { title: 'Surf Workouts', path: `athlete/${id}/water`, cName: 'sidebar-text' },
    { title: 'Tournaments', path: `athlete/${id}/tournaments`, cName: 'sidebar-text' },
  ]

  return <Bars title={athleteName} sidebarData={sidebarItems} />
}
