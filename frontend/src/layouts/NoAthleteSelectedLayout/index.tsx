import * as React from 'react'
import { Outlet } from 'react-router-dom'

import { Bars } from '../../components/Bars'

import styles from './styles.module.css'

export function NoAthleteSelectedLayout() {
  return (
    <>
      <Bars title="Wave Coach" sideBarItems={[]} />
      <div className={styles.container}>
        <Outlet />
      </div>
    </>
  )
}
