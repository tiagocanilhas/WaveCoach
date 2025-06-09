import * as React from 'react'
import { useReducer } from 'react'
import { View, Text } from 'react-native'
import WheelPicker from 'react-native-wheel-picker-expo'

import { styles } from './styles'

type State = {
  hours: number
  minutes: number
  seconds: number
}

type Action =
  | { type: 'setHours'; payload: number }
  | { type: 'setMinutes'; payload: number }
  | { type: 'setSeconds'; payload: number }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'setHours':
      return { ...state, hours: action.payload }
    case 'setMinutes':
      return { ...state, minutes: action.payload }
    case 'setSeconds':
      return { ...state, seconds: action.payload }
    default:
      return state
  }
}

type DurationPickerProps = {
  value: number
  onChange: (value: number) => void
}

export function DurationPicker({ value, onChange }: DurationPickerProps) {
  const initialState: State = {
    hours: Math.floor(value / 3600),
    minutes: Math.floor((value % 3600) / 60),
    seconds: value % 60,
  }

  const [state, dispatch] = useReducer(reducer, initialState)

  const hourOptions = Array.from({ length: 1000 }, (_, i) => `${i}`)
  const minuteOptions = Array.from({ length: 60 }, (_, i) => `${i}`)
  const secondOptions = Array.from({ length: 60 }, (_, i) => `${i}`)

  const hours = state.hours
  const minutes = state.minutes
  const seconds = state.seconds

  return (
    <View style={styles.container}>
      <View style={styles.inputContainer}>
        <Text style={styles.label}>Hours</Text>
        <View style={styles.wheelPickerContainer}>
          <WheelPicker
            backgroundColor={styles.wheelPicker.color}
            width={styles.wheelPicker.width}
            height={styles.wheelPicker.height}
            initialSelectedIndex={hours}
            items={hourOptions.map(item => ({ label: item, value: item }))}
            onChange={({ index }) => dispatch({ type: 'setHours', payload: index })}
          />
        </View>
      </View>
      <View style={styles.inputContainer}>
        <Text style={styles.label}>Minutes</Text>
        <View style={styles.wheelPickerContainer}>
          <WheelPicker
            backgroundColor={styles.wheelPicker.color}
            width={styles.wheelPicker.width}
            height={styles.wheelPicker.height}
            initialSelectedIndex={minutes}
            items={minuteOptions.map(item => ({ label: item, value: item }))}
            onChange={({ index }) => dispatch({ type: 'setMinutes', payload: index })}
          />
        </View>
      </View>
      <View style={styles.inputContainer}>
        <Text style={styles.label}>Seconds</Text>
        <View style={styles.wheelPickerContainer}>
          <WheelPicker
            backgroundColor={styles.wheelPicker.color}
            width={styles.wheelPicker.width}
            height={styles.wheelPicker.height}
            initialSelectedIndex={seconds}
            items={secondOptions.map(item => ({ label: item, value: item }))}
            onChange={({ index }) => dispatch({ type: 'setSeconds', payload: index })}
          />
        </View>
      </View>
    </View>
  )
}
