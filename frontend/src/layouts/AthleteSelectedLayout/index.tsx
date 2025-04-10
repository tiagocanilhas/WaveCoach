import * as React from 'react'
import { useState, useEffect } from 'react'
import { Outlet, useParams } from 'react-router-dom'

import { Bars } from '../../components/Bars'

export function AthleteSelectedLayout() {
  const [athleteName, setAthleteName] = useState('')
  const id = useParams().aid

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
    { title: 'Athlete Profile', path: `athlete/${id}` },
    { title: 'Characteristics', path: `athlete/${id}/characteristics` },
    { title: 'Gym Workouts', path: `athlete/${id}/gym` },
    { title: 'Surf Workouts', path: `athlete/${id}/water` },
    { title: 'Tournaments', path: `athlete/${id}/tournaments` },
  ]

  return (
    <>
      <Bars title={athleteName} sideBarItems={sidebarItems}/>
      <Outlet />
    </>
  )
}

