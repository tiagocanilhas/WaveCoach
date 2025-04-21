import * as React from 'react'
import { Navigate, Outlet, useLocation } from 'react-router-dom'

import { CircularProgress } from '@mui/material'

import { useAuthentication } from '../../hooks/useAuthentication'

import styles from './styles.module.css'

export function RequireNoAuthentication() {
  const [user, _, loading] = useAuthentication()
  const location = useLocation()

  if (loading) return <CircularProgress className={styles.waiting} />

  if (!user) return <Outlet />

  return <Navigate to="/" state={{ source: location.state?.source || '/' }} replace={true} />
}
