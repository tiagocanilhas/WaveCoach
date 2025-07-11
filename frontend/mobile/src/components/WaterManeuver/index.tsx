import * as React from 'react'
import { View, Text, Image, TouchableOpacity } from 'react-native'

import { epochConverter } from '@utils/epochConverter'

import { WaterManeuver as WaterManeuverType } from '@types/WaterManeuver'

import { styles } from './styles'

export type WaterManeuverProps = {
  maneuver: WaterManeuverType
  onPress?: (Maneuver: WaterManeuverType) => void
}

export function WaterManeuver({ maneuver, onPress }: WaterManeuverProps) {
  return (
    <TouchableOpacity onPress={() => onPress?.(maneuver)}>
      <View style={styles.container}>
        <Image style={styles.image} source={maneuver.url ? { uri: maneuver.url } : require('../../../assets/no_image.png')} />
        <Text style={styles.text} numberOfLines={1} ellipsizeMode="tail">
          {maneuver.name}
        </Text>
      </View>
    </TouchableOpacity>
  )
}
