import * as React from 'react'
import { View, Text, Image, TouchableOpacity } from 'react-native'

import { epochConverterToAge } from '@utils/epochConverterToAge'

import { Athlete as AthleteType } from '@types/Athlete'

import { styles } from './styles'

export type AthleteExtendedProps = {
  athlete: AthleteType
  onPress?: (athlete: AthleteType) => void
}

export function AthleteExtended({ athlete, onPress }: AthleteExtendedProps) {
  return (
    <TouchableOpacity onPress={() => onPress?.(athlete)}>
      <View style={styles.container}>
        <View style={styles.imageContainer}>
          <Image
            style={styles.image}
            source={athlete.url ? { uri: athlete.url } : require('../../../assets/anonymous-user.webp')}
          />
        </View>
        <View style={styles.infoContainer}>
          <Text style={styles.text} numberOfLines={1} ellipsizeMode="tail">
            {athlete.name}
          </Text>
          <Text style={styles.text} numberOfLines={1} ellipsizeMode="tail">
            {epochConverterToAge(athlete.birthdate)} Years Old
          </Text>
        </View>
      </View>
    </TouchableOpacity>
  )
}
