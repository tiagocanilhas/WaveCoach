import * as React from 'react'
import { ActivityIndicator } from 'react-native'
import { MainView } from '@components/MainView'

import { styles } from './styles'

export function Loading() {
  return (
    <MainView>
      <ActivityIndicator style={styles.activityIndicator} size={styles.size} color={styles.color} />
    </MainView>
  )
}
