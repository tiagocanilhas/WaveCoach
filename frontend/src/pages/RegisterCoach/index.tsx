import * as React from 'react'

import { RegisterForm } from '../../components/RegisterForm'

import { createCoach } from '../../services/coachServices'

import styles from './styles.module.css'

export function RegisterCoach() {
  async function handleOnSubmit(username: string, password: string) {
    return await createCoach(username, password)
  }

  return (
    <div className={styles.container}>
      <RegisterForm title="Register Coach" initialUsername="" buttonText="Register" onSubmit={handleOnSubmit} />
    </div>
  )
}
