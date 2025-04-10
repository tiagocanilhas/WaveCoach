import * as React from 'react'

import { Bars } from '../../components/Bars'
import { Outlet } from 'react-router-dom'

export function NoAthleteSelectedLayout() {
  return (
    <>
      <Bars title="WaveCoach"sideBarItems={[]} />
      <Outlet />
    </>
  )
}
