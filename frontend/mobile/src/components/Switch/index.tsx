import React from 'react'
import { View, Text, Switch as SwitchRN } from 'react-native'

import { styles } from './styles'

type SwitchProps = {
  value: boolean
  onChange: (value: boolean) => void
  leftLabel?: string
  rightLabel?: string
  labelStyle?: object
}

export function Switch({ value, onChange, leftLabel, rightLabel, labelStyle }: SwitchProps) {
  return (
    <View style={styles.container}>
      {leftLabel && <Text style={[styles.label, labelStyle]}>{leftLabel}</Text>}
      <SwitchRN value={value} onValueChange={onChange} />
      {rightLabel && <Text style={[styles.label, labelStyle]}>{rightLabel}</Text>}
    </View>
  )
}
