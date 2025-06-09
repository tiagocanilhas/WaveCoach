import * as React from 'react'

import { TextInput, ViewStyle, StyleProp } from 'react-native'

import { styles } from './styles'

type InputProps = {
  style?: StyleProp<ViewStyle>
  value: string
  placeholder: string
  secureTextEntry?: boolean
  onChange: (text: string) => void
}

export function Input({ style, value, placeholder, secureTextEntry, onChange }: InputProps) {
  return (
    <TextInput
      style={[styles.input, style]}
      placeholderTextColor={styles.placeholderTextColor}
      value={value}
      onChangeText={onChange}
      placeholder={placeholder}
      secureTextEntry={secureTextEntry}
    />
  )
}
