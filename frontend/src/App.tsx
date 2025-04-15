import * as React from 'react'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import { Helmet } from 'react-helmet'

import { Login } from './pages/Login'
import { RegisterSelect } from './pages/RegisterSelect'
import { RegisterCoach } from './pages/RegisterCoach'
import { RegisterAthleteCode } from './pages/RegisterAthleteCode'
import { RegisterAthlete } from './pages/RegisterAthlete'
import { Home } from './pages/Home'
import { Athlete } from './pages/Athlete'
import { NotFound } from './pages/NotFound'
import { Characteristics } from './pages/Characterisitcs'
import { NoAthleteSelectedLayout } from './layouts/NoAthleteSelectedLayout'
import { AthleteSelectedLayout } from './layouts/AthleteSelectedLayout'

import './global.css'

const router = createBrowserRouter([
  { path: '/login', element: <Title title="Login" content={<Login />} /> },
  { path: '/register', element: <Title title="Register" content={<RegisterSelect />} /> },
  { path: '/register/coach', element: <Title title="Register Coach" content={<RegisterCoach />} /> },
  { path: '/register/athlete-code', element: <Title title="Register Athlete Code" content={<RegisterAthleteCode />} /> },
  { path: '/register/athlete', element: <Title title="Register Athlete" content={<RegisterAthlete />} /> },

  {
    path: '/',
    element: <NoAthleteSelectedLayout />,
    children: [{ path: '/', element: <Title title="Home" content={<Home />} /> }],
  },

  {
    path: '/',
    element: <AthleteSelectedLayout />,
    children: [
      { path: 'athletes/:aid', element: <Title title="Athlete" content={<Athlete />} /> },
      { path: 'athletes/:aid/characteristics', element: <Title title="Characteristics" content={<Characteristics />} /> },
    ],
  },

  { path: '*', element: <NotFound /> },
])

export function App() {
  return <RouterProvider router={router} />
}

type TitleProps = {
  title: string
  content: React.ReactNode
}

function Title({ title, content }: TitleProps) {
  const titlePrefix = 'Wave Coach'
  return (
    <>
      <Helmet>
        <title>{`${titlePrefix} - ${title}`}</title>
      </Helmet>
      {content}
    </>
  )
}
