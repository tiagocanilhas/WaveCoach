import * as React from 'react'
import { useReducer } from 'react'
import { RouteProp, useRoute } from '@react-navigation/native'

import { RootStackParamList } from '@components/Navigator'

import { Input } from '@components/Input'
import { MainView } from '@components/MainView'
import { Slider } from '@components/Slider'

import { styles } from './style'
import { View } from 'react-native'
import { DurationPicker } from '@components/DurationPicker'

type AddingWaterWorkoutRouteProp = RouteProp<RootStackParamList, 'AddingWaterWorkout'>

type State = {
  condition: string
  rpe: number
  trimp: number
  time: number
  waves: any[]
}

type Action = { type: 'setNewValue'; name: string; value: any } | { type: 'addWave'; wave: any }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'setNewValue':
      return { ...state, [action.name]: action.value }
    case 'addWave':
      return { ...state, waves: [...state.waves, action.wave] }
    default:
      return state
  }
}

export function AddingWaterWorkout() {
  const initialState: State = {
    condition: '',
    rpe: 1,
    time: 0,
    trimp: 50,
    waves: [],
  }
  const [state, dispatch] = useReducer(reducer, initialState)

  const route = useRoute<AddingWaterWorkoutRouteProp>()
  const { athlete } = route.params

  function handleOnChange(name: string, value: any) {
    dispatch({ type: 'setNewValue', name, value })
  }

  const condition = state.condition
  const rpe = state.rpe
  const time = state.time
  const trimp = state.trimp
  const waves = state.waves

  return (
    <MainView style={styles.container}>
      <View style={styles.inputContainer}>
        <Input value={condition} placeholder="Condition" onChange={v => handleOnChange('condition', v)} />
        <Slider
          label="RPE"
          value={rpe}
          minimumValue={1}
          maximumValue={10}
          step={1}
          onValueChange={v => handleOnChange('rpe', v)}
        />
        <Slider
          label="TRIMP"
          value={trimp}
          minimumValue={50}
          maximumValue={190}
          step={1}
          onValueChange={v => handleOnChange('trimp', v)}
        />
        <DurationPicker value={time} onChange={v => handleOnChange('time', v)} />
      </View>
    </MainView>
  )
}
