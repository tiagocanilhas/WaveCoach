import * as React from 'react'

import { RegisterForm } from '../components/RegisterForm'

export function RegisterCoach() {
  async function handleOnSubmit(username: string, password: string, confirmPassword: string) {
    await Promise.reject()
  }

  return <RegisterForm title="Register Coach" initialUsername="" buttonText="Register" onSubmit={handleOnSubmit} />
}
