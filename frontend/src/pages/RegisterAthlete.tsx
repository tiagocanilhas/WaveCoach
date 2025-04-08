import * as React from 'react'

import { RegisterForm } from '../components/RegisterForm'

export function RegisterAthlete() {
  async function handleOnSubmit(username: string, password: string, confirmPassword: string) {
    await Promise.reject()
  }

  return (
    <RegisterForm
      title="Register Athlete"
      initialUsername="Athlete_123123123"
      buttonText="Change Credentials"
      onSubmit={handleOnSubmit}
    />
  )
}
