import * as React from 'react'
import { Link, useNavigate } from 'react-router-dom'

import { Card } from '../../components/Card'
import { Button } from '../../components/Button'

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
            <h1 className={styles.title}>Register</h1>
            <div className={styles.buttons}>
              <Button text="Coach" onClick={handleOnClickCoach} width="100%" height="25px" />
              <Button text="Athlete" onClick={handleOnClickAthlete} width="100%" height="25px" />
            </div>
            <p className={styles.text}>
              Have an account? <Link to="/login">Login</Link>
            </p>
          </>
        }
      />
    </div>
  )
}
