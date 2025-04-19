import * as React from 'react'
import { useLocation, Navigate, Outlet } from 'react-router-dom'

import { CircularProgress } from '@mui/material'

import { useAuthentication } from '../../hooks/useAuthentication'

import styles from './styles.module.css'

export function RequireAuthentication() {
  const [user, _, loading] = useAuthentication()
  const location = useLocation()

  if (loading) return <CircularProgress className={styles.waiting} />
  
  if (!user) return <Navigate to="/login" state={{ source: location.pathname }} replace />
  
  return <Outlet />
}
