import * as React from 'react'
import {
  Keyboard,
  KeyboardAvoidingView,
  Platform,
  TouchableWithoutFeedback,
  SafeAreaView,
  StyleProp,
  ViewStyle,
} from 'react-native'

import { styles } from './styles'

type MainViewProps = {
  style?: StyleProp<ViewStyle>
  children?: React.ReactNode
}

export function MainView({ style, children }: MainViewProps) {
  return (
    <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === 'ios' ? 'padding' : 'height'}>
      <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
        <SafeAreaView style={[styles.container, style]}>{children}</SafeAreaView>
      </TouchableWithoutFeedback>
    </KeyboardAvoidingView>
  )
}
