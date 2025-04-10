import * as React from 'react'
import { useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'

import { Card } from '../../components/Card'

import { RegisterForm } from '../../components/RegisterForm'
import { register } from '../../services/userServices'
import { CircularProgress } from '@mui/material'

import styles from './styles.module.css'

export function RegisterAthlete() {
  const [username, setUsername] = React.useState('')

  const navigate = useNavigate()
  const [params] = useSearchParams()
  const code = params.get('code')

  useEffect(() => {
    async function getUsername() {
      try {
        const res = await getUserByCode(code)
        setUsername(res.username)
      } catch (e) {
        navigate('/register/athlete-code')
      }
    }
    getUsername()
  }, [])

  async function handleOnSubmit(username: string, password: string) {
    return await register(username, password)
  }

  return username === '' ? (
    <div className={styles.container}>
      <Card content={<CircularProgress />} />
    </div>
  ) : (
    <div className={styles.container}>
      <RegisterForm
        title="Register Athlete"
        initialUsername={username}
        buttonText="Change Credentials"
        onSubmit={handleOnSubmit}
      />
    </div>
  )
}

async function getUserByCode(code: string): Promise<{ username: string }> {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (code === 'valid-code') {
        resolve({ username: 'Athlete_1231231' })
      } else {
        reject(new Error('Invalid code'))
      }
    }, 2000)
  })
}
