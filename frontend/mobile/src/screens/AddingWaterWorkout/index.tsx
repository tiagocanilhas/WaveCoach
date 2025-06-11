import * as React from 'react'
import { useReducer, useRef } from 'react'
import { Alert, ScrollView, Text, TouchableOpacity } from 'react-native'
import { RouteProp, useNavigation, useRoute } from '@react-navigation/native'
import DraggableFlatList, { RenderItemParams } from 'react-native-draggable-flatlist'

import { RootStackParamList } from '@components/Navigator'

import { Input } from '@components/Input'
import { MainView } from '@components/MainView'
import { Slider } from '@components/Slider'

import { styles } from './style'
import { View } from 'react-native'
import { DurationPicker } from '@components/DurationPicker'
import { VerticalReorderableList } from '@components/VerticalReorderableList'
import { Button } from '@components/Button'
import { Header } from '@components/Header'

type AddingWaterWorkoutRouteProp = RouteProp<RootStackParamList, 'AddingWaterWorkout'>

type State = {
  condition: string
  rpe: number
  trimp: number
  time: number
  wave: any | null
  waves: any[]
}

type Action =
  | { type: 'setNewValue'; name: string; value: any }
  | { type: 'addWave'; wave: any }
  | { type: 'waveToEdit'; wave: any }
  | { type: 'updateWave'; wave: any }
  | { type: 'removeWave'; id: string }
  | { type: 'setWaves'; waves: any[] }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'setNewValue':
      return { ...state, [action.name]: action.value }
    case 'addWave':
      return { ...state, waves: [...state.waves, action.wave] }
    case 'waveToEdit':
      return { ...state, wave: action.wave }
    case 'updateWave':
      return {
        ...state,
        waves: state.waves.map(w => (w.id === action.wave.id ? action.wave : w)),
      }
    case 'removeWave':
      return {
        ...state,
        waves: state.waves.filter(w => w.id !== action.id),
      }
    case 'setWaves':
      return { ...state, waves: action.waves }
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
    wave: null,
    waves: [],
  }
  const [state, dispatch] = useReducer(reducer, initialState)
  const navigation = useNavigation();

  const route = useRoute<AddingWaterWorkoutRouteProp>()
  const { athlete } = route.params

  function handleOnChange(name: string, value: any) {
    dispatch({ type: 'setNewValue', name, value })
  }

  function reorderWaves(waves: any[]) {
    dispatch({ type: 'setWaves', waves })
  }

  async function handleSave() {
    
    // Save logic goes here.

    Alert.alert(
      'Success',
      'Workout saved successfully!',
      [
        { 
          text: 'OK', 
          onPress: () => navigation.goBack() 
        }
      ]
    );
  }

  function handleBackPress(changesDone: boolean) {
    if (changesDone) {
      navigation.goBack();
      return;
    }
    
    Alert.alert(
      'Discard changes?',
      'Are you sure you want to leave?',
      [
        { text: 'Cancel', style: 'cancel' },
        { text: 'Save', onPress: handleSave },
        { text: 'Discard', style: 'destructive', onPress: () => navigation.goBack() },
      ]
    );
  };

  const condition = state.condition
  const rpe = state.rpe
  const time = state.time
  const trimp = state.trimp
  const waves = state.waves
  
  const disabled = (Object.keys(initialState) as Array<keyof State>).every(key =>
    Array.isArray(state[key]) 
      ? state[key].length === initialState[key].length 
      : state[key] === initialState[key]
  );

  return (
    <MainView style={styles.container}>
      <Header
        title="Back"
        onBackPress={() => handleBackPress(disabled)}
      />

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

      <VerticalReorderableList<number>
        data={waves}
        onDragEnd={reorderWaves}
        keyExtractor={item => item.toString()}
        renderItem={(item, drag, isActive) => (
          <Text
            style={{
              fontSize: 16,
              padding: 10,
              backgroundColor: isActive ? '#ddd' : '#fff',
              marginVertical: 5,
              borderRadius: 5,
            }}
            onLongPress={drag}
          >
            Wave {item}
          </Text>
        )}
        onAdd={() => {}}
      />

      <View style={styles.footerContainer}>
        <Button text="Save" onPress={handleSave} disabled={disabled} />
      </View>

    </MainView>
  )
}
