import * as React from 'react'

import { RegisterForm } from '../../components/RegisterForm'
import { register } from '../../services/userServices'

import styles from './styles.module.css'

export function RegisterCoach() {
  async function handleOnSubmit(username: string, password: string) {
    return await register(username, password)
  }

  return (
    <div className={styles.container}>
      <RegisterForm title="Register Coach" initialUsername="" buttonText="Register" onSubmit={handleOnSubmit} />
    </div>
  )
}
