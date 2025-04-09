import * as React from 'react'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'

import { Login } from './pages/Login'
import { RegisterSelect } from './pages/RegisterSelect'
import { RegisterCoach } from './pages/RegisterCoach'
import { RegisterAthleteCode } from './pages/RegisterAthleteCode'
import { RegisterAthlete } from './pages/RegisterAthlete'
import { Home } from './pages/Home'
import { Athlete } from './pages/Athlete'
import { NotFound } from './pages/NotFound'
import { Characteristics } from './pages/Characterisitcs'
import { NoAthleteSelectedLayout } from './pages/NoAthleteSelectedLayout'
import { AthleteSelectedLayout } from './pages/AthleteSelectedLayout'

import './styles/styles.css'

const router = createBrowserRouter([
  { path: '/login', element: <Login /> },
  { path: '/register', element: <RegisterSelect /> },
  { path: '/register/coach', element: <RegisterCoach /> },
  { path: '/register/athlete-code', element: <RegisterAthleteCode /> },
  { path: '/register/athlete', element: <RegisterAthlete /> },

  { path: '/', element: <NoAthleteSelectedLayout />, children: [{ path: '/', element: <Home /> }] },

  {
    path: '/',
    element: <AthleteSelectedLayout />,
    children: [
      { path: 'athlete/:aid', element: <Athlete /> },
      { path: 'athlete/:aid/characteristics', element: <Characteristics /> },
    ],
  },

  { path: '*', element: <NotFound /> },
])

export function App() {
  return <RouterProvider router={router} />
}
