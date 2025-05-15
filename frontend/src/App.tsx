import * as React from 'react'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'

import { Title } from './components/Title'
import { AuthenticationProvider } from './components/AuthenticationProvider'
import { RequireNoAuthentication } from './components/RequireNoAuthentication'
import { RequireAuthentication } from './components/RequireAuthentication'

import { NoAthleteSelectedLayout } from './layouts/NoAthleteSelectedLayout'
import { Home } from './pages/Home'
import { Account } from './pages/Account'

import { AthleteSelectedLayout } from './layouts/AthleteSelectedLayout'
import { Athlete } from './pages/Athlete'
import { Characteristics } from './pages/Characteristics'
import { GymWorkouts } from './pages/GymWorkouts'
import { GymWorkoutsDetails } from './pages/GymWorkoutsDetails'
import { WaterWorkouts } from './pages/WaterWorkouts'

import { Login } from './pages/Login'
import { RegisterSelect } from './pages/RegisterSelect'
import { RegisterAthlete } from './pages/RegisterAthlete'
import { RegisterAthleteCode } from './pages/RegisterAthleteCode'
import { RegisterCoach } from './pages/RegisterCoach'
import { NotFound } from './pages/NotFound'

import './global.css'

const router = createBrowserRouter([
  {
    path: '/',
    element: <RequireAuthentication />,
    children: [
      {
        element: <NoAthleteSelectedLayout />,
        children: [
          { path: '', element: <Title title="Home" content={<Home />} /> },
          { path: 'account', element: <Title title="Account" content={<Account />} /> },
        ],
      },
      {
        element: <AthleteSelectedLayout />,
        children: [
          { path: 'athletes/:aid', element: <Title title="Athlete" content={<Athlete />} /> },
          { path: 'athletes/:aid/characteristics', element: <Title title="Characteristics" content={<Characteristics />} /> },
          { path: 'athletes/:aid/gym/:wid', element: <Title title="Gym Workouts Details" content={<GymWorkoutsDetails />} /> },
          { path: 'athletes/:aid/gym', element: <Title title="Gym" content={<GymWorkouts />} /> },
          { path: 'athletes/:aid/water', element: <Title title="Water" content={<WaterWorkouts />} /> },
        ],
      },
    ],
  },
  {
    path: '/',
    element: <RequireNoAuthentication />,
    children: [
      { path: 'login', element: <Title title="Login" content={<Login />} /> },
      { path: 'register', element: <Title title="Register" content={<RegisterSelect />} /> },
      { path: 'register/coach', element: <Title title="Register Coach" content={<RegisterCoach />} /> },
      { path: 'register/athlete-code', element: <Title title="Register Athlete Code" content={<RegisterAthleteCode />} /> },
      { path: 'register/athlete', element: <Title title="Register Athlete" content={<RegisterAthlete />} /> },
    ],
  },
  { path: '*', element: <NotFound /> },
])

export function App() {
  return (
    <AuthenticationProvider>
      <RouterProvider router={router} />
    </AuthenticationProvider>
  )
}
