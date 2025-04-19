import * as React from 'react'
import { useState, useEffect } from 'react'
import { Outlet, useParams } from 'react-router-dom'

import { Bars } from '../../components/Bars'

import { getAthlete } from '../../services/athleteServices'

import styles from './styles.module.css'

export function AthleteSelectedLayout() {
  const [athleteName, setAthleteName] = useState('Wave Coach')
  const id = useParams().aid

  useEffect(() => {
    async function fetchAthleteName() {
      try {
        const res = await getAthlete(id)
        setAthleteName(res.name)
      } catch (error) {
        // Handle error if needed
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
      <div className={styles.container}>
        <Outlet />
      </div>
    </>
  )
}
