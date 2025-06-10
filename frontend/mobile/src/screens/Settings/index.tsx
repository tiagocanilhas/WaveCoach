import * as React from 'react'
import { Alert, Text } from 'react-native'

import { MainView } from '@components/MainView'
import { Button } from '@components/Button'

import { useAuthentication } from '@hooks/useAuthentication'

import { logout } from '@services/userServices'

import { Storage } from '@storage/Storage'

import { styles } from './styles'

export function Settings() {
  const [_, setUser] = useAuthentication()

  function handleLogout() {
    Alert.alert(
      'Logout',
      'Are you sure you want to logout?',
      [
        {
          text: 'Cancel',
          style: 'cancel',
        },
        {
          text: 'Logout',
          onPress: async () => {
            const token = await Storage.getToken()
            await logout(token ?? '')
            await Storage.clearAll()
            setUser(undefined)
          },
          style: 'destructive',
        },
      ],
      { cancelable: false }
    )
  }

  return (
    <MainView>
      <Text style={styles.title}>Settings</Text>
      <Button text="Logout" onPress={handleLogout} />
    </MainView>
  )
}
