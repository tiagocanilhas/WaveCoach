import * as React from 'react'
import { Bars } from '../components/Bars'

export function NoAthleteSelectedLayout() {
  const sidebarItems = []

  return <Bars title="WaveCoach" sidebarData={sidebarItems} />
}
