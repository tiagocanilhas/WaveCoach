import * as React from 'react'
import { View, Text, Image, TouchableOpacity } from 'react-native'

import { epochConverter } from '@utils/epochConverter'

import { Athlete as AthleteType } from '@types/Athlete'

import { styles } from './styles'

export type AthleteProps = {
  athlete: AthleteType
  onPress?: (athlete: AthleteType) => void
}

export function Athlete({ athlete, onPress }: AthleteProps) {
  return (
    <TouchableOpacity onPress={() => onPress?.(athlete)}>
      <View style={styles.container}>
        <Image
          style={styles.image}
          source={athlete.url ? { uri: athlete.url } : require('../../../assets/anonymous-user.webp')}
        />
        <Text style={styles.text} numberOfLines={1} ellipsizeMode="tail">
          {athlete.name}
        </Text>
      </View>
    </TouchableOpacity>
  )
}
