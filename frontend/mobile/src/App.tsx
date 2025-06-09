import * as React from 'react'
import { Navigator } from '@components/Navigator'
import { AuthenticationProvider } from '@components/AuthenticationProvider'

export function App() {
  return (
    <AuthenticationProvider>
      <Navigator />
    </AuthenticationProvider>
  )
}
