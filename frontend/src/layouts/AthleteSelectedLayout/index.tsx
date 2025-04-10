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
        setAthleteName('Wave Coach')
      }
    }
    fetchAthleteName()
  }, [])

  const sidebarItems = [
    { title: 'Athlete Profile', path: `athletes/${id}` },
    { title: 'Characteristics', path: `athletes/${id}/characteristics` },
    { title: 'Gym Workouts', path: `athletes/${id}/gym` },
    { title: 'Surf Workouts', path: `athletes/${id}/water` },
    { title: 'Tournaments', path: `athletes/${id}/tournaments` },
  ]

  return (
    <>
      <Bars title={athleteName} sideBarItems={sidebarItems} />
      <Outlet />
    </>
  )
}
