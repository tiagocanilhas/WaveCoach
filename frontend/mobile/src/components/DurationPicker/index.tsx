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

type Action = { type: 'setValue'; name: string; value: number }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'setValue':
      return {
        ...state,
        [action.name]: action.value,
      }
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

  function handleOnChange(name: string, value: number) {
    dispatch({ type: 'setValue', name, value })
    const totalSeconds = state.hours * 3600 + state.minutes * 60 + state.seconds
    onChange(totalSeconds)
  }

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
            onChange={({ index }) => handleOnChange('hours', index)}
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
            onChange={({ index }) => handleOnChange('minutes', index)}
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
            onChange={({ index }) => handleOnChange('seconds', index)}
          />
        </View>
      </View>
    </View>
  )
}
