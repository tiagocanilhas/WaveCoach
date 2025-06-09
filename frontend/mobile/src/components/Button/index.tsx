import * as React from 'react'

import { Pressable, Text } from 'react-native'

import { styles } from './styles'

type ButtonProps = {
  text: String
  onPress: () => void
  disabled?: boolean
}

export function Button({ text, onPress, disabled = false }: ButtonProps) {
  return (
    <Pressable
      style={({ pressed }) => [styles.button, pressed && styles.pressed, disabled && styles.disabled]}
      onPress={onPress}
      disabled={disabled}
    >
      <Text style={styles.text}>{text}</Text>
    </Pressable>
  )
}
