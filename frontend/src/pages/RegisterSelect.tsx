import * as React from 'react'
import { useNavigate } from 'react-router-dom'

import { Card } from '../components/Card'

export function RegisterSelect() {
  const navigate = useNavigate()

  function handleOnClickCoach() {
    navigate('/register/coach')
  }

  function handleOnClickAthlete() {
    navigate('/register/athlete-code')
  }

  return (
    <Card
      content={
        <>
          <h1>Register</h1>
          <button onClick={handleOnClickCoach}>Coach</button>
          <button onClick={handleOnClickAthlete}>Athlete</button>
        </>
      }
    />
  )
}
