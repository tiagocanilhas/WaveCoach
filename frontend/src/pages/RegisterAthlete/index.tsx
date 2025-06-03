import * as React from 'react'
import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'

import { CircularProgress } from '@mui/material'
import { RegisterForm } from '../../components/RegisterForm'

import { getAthleteByCode, changeAthleteCredentials } from '../../../services/athleteServices'

import styles from './styles.module.css'

export function RegisterAthlete() {
  const [username, setUsername] = useState<string>(undefined)

  const navigate = useNavigate()
  const [params, _] = useSearchParams()
  const code = params.get('code')

  useEffect(() => {
    async function getUsername() {
      try {
        const res = await getAthleteByCode(code)
        setUsername(res.username)
      } catch (e) {
        navigate('/register/athlete-code')
      }
    }
    getUsername()
  }, [])

  async function handleOnSubmit(username: string, password: string) {
    return await changeAthleteCredentials(code, username, password)
  }

  return (
    <div className={styles.container}>
      {username === undefined 
        ? <CircularProgress />
        : (
        <RegisterForm
          title="Register Athlete"
          initialUsername={username}
          buttonText="Change Credentials"
          onSubmit={handleOnSubmit}
        />
      )}
    </div>
  )
}
