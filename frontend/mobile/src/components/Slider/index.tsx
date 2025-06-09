import * as React from 'react'
import { View, Text } from 'react-native'
import SliderRN from '@react-native-community/slider'

import { styles, minimumTrackTintColor, maximumTrackTintColor, thumbTintColor } from './styles'

type SliderProps = {
  label: string
  value: number
  minimumValue: number
  maximumValue: number
  step: number
  onValueChange: (value: number) => void
}

export function Slider({ label, value, minimumValue, maximumValue, step, onValueChange }: SliderProps) {
  return (
    <View style={styles.container}>
      <Text style={styles.label}>{label}</Text>
      <SliderRN
        style={styles.slider}
        value={value}
        minimumValue={minimumValue}
        maximumValue={maximumValue}
        step={step}
        onValueChange={onValueChange}
        minimumTrackTintColor={minimumTrackTintColor}
        maximumTrackTintColor={maximumTrackTintColor}
        thumbTintColor={thumbTintColor}
      />
      <Text style={styles.value}>{value}</Text>
    </View>
  )
}
