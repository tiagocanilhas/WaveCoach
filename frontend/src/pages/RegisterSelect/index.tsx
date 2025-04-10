import * as React from 'react'
import { Link, useNavigate } from 'react-router-dom'

import { Card } from '../../components/Card'

import styles from './styles.module.css'

export function RegisterSelect() {
  const navigate = useNavigate()

  function handleOnClickCoach() {
    navigate('/register/coach')
  }

  function handleOnClickAthlete() {
    navigate('/register/athlete-code')
  }

  return (
    <div className={styles.container}>
      <Card
        content={
          <>
            <h1>Register</h1>
            <div className={styles.buttons}>
              <button onClick={handleOnClickCoach}>Coach</button>
              <button onClick={handleOnClickAthlete}>Athlete</button>
            </div>
            <p>
              Have an account? <Link to="/login">Login</Link>
            </p>
          </>
        }
      />
    </div>
  )
}
