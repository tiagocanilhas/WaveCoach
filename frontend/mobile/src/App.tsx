import * as React from 'react'
import { Navigator } from '@components/Navigator'
import { AuthenticationProvider } from '@components/AuthenticationProvider'
import { GestureHandlerRootView } from 'react-native-gesture-handler'

export function App() {
  return (
    <GestureHandlerRootView>
      <AuthenticationProvider>
        <Navigator />
      </AuthenticationProvider>
    </GestureHandlerRootView>
  )
}
