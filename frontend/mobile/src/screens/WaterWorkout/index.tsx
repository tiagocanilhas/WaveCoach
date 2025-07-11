import * as React from 'react'
import { useReducer } from 'react'
import { View, Alert, Text } from 'react-native'
import { NavigationProp, RouteProp, useNavigation, useRoute } from '@react-navigation/native'
import { Entypo, Ionicons } from '@expo/vector-icons'

import { RootStackParamList } from '@components/Navigator'
import { Input } from '@components/Input'
import { MainView } from '@components/MainView'
import { Slider } from '@components/Slider'
import { DurationPicker } from '@components/DurationPicker'
import { VerticalReorderableList } from '@components/VerticalReorderableList'
import { Button } from '@components/Button'
import { SwipeableItem } from '@components/SwipeableItem'
import { Header } from '@components/Header'

import { Wave } from '@types/Wave'

import { WorkoutEditing } from '@utils/WorkoutEditing'
import { handleError } from '@utils/handleError'
import { diffListOrNull } from '@utils/diffListOrNull'

import { createWaterActivity, updateWaterActivity } from '@services/waterServices'

import { styles } from './styles'

type State = {
  condition: string
  rpe: number
  trimp: number
  time: number
  editableWaves: Wave[]
  removedWaves: Wave[]
  waveToEdit: Wave | null
}

type Action =
  | { type: 'setNewValue'; name: string; value: any }
  | { type: 'addWave'; wave: Wave }
  | { type: 'waveToEdit'; wave: Wave }
  | { type: 'updateWave'; wave: Wave }
  | { type: 'removeWave'; id: number | null; tempId?: number }
  | { type: 'setWaves'; waves: Wave[] }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'setNewValue':
      return { ...state, [action.name]: action.value }
    case 'addWave':
      return { ...state, editableWaves: [...state.editableWaves, action.wave] }
    case 'waveToEdit':
      return { ...state, waveToEdit: action.wave }
    case 'updateWave':
      return {
        ...state,
        editableWaves: state.editableWaves.map(wave => {
          const matchById = wave.id !== null && wave.id === action.wave.id
          const matchByTempId = wave.id === null && wave.tempId === action.wave.tempId
          return matchById || matchByTempId ? action.wave : wave
        }),
        waveToEdit: null,
      }
    case 'removeWave':
      const removedWave = state.editableWaves.find(w => (action.id === null ? w.tempId === action.tempId : w.id === action.id))
      if (!removedWave) return state

      return {
        ...state,
        editableWaves: state.editableWaves.filter(w => w !== removedWave),
        removedWaves: [...state.removedWaves, WorkoutEditing.nullifyFieldsExceptId(removedWave)],
      }
    case 'setWaves':
      return { ...state, editableWaves: action.waves }
    default:
      return state
  }
}

export function WaterWorkout() {
  const navigation = useNavigation<NavigationProp<RootStackParamList, 'WaterWorkout'>>()
  const route = useRoute<RouteProp<RootStackParamList, 'WaterWorkout'>>()
  const { athlete, workout } = route.params

  const initialState: State = {
    condition: workout ? workout.condition : '',
    rpe: workout ? workout.rpe : 1,
    time: workout ? workout.time : 0,
    trimp: workout ? workout.trimp : 50,
    editableWaves: workout ? workout.waves : [],
    removedWaves: [],
    waveToEdit: null,
  }
  const [state, dispatch] = useReducer(reducer, initialState)
  function handleOnChange(name: string, value: any) {
    dispatch({ type: 'setNewValue', name, value })
  }

  function reorderWaves(waves: any[]) {
    dispatch({ type: 'setWaves', waves })
  }

  async function handleSave() {
    try {
      if (workout) {
        const condition = state.condition === workout.condition ? undefined : state.condition
        const rpe = state.rpe === workout.rpe ? undefined : state.rpe
        const time = state.time === workout.time ? undefined : state.time
        const trimp = state.trimp === workout.trimp ? undefined : state.trimp

        const waves =
          diffListOrNull(state.editableWaves, (wave, index) => {
            const original = initialState.editableWaves.find(w => w.id === wave.id)

            const newWave = {
              id: wave.id,
              rightSide: WorkoutEditing.onlyIfDifferent('rightSide', wave, original || {}),
              maneuvers: diffListOrNull(wave.maneuvers, (maneuver, mIndex) => {
                const originalManeuver = original?.maneuvers.find(m => m.id === maneuver.id) || {}

                const newManeuver = {
                  id: maneuver.id,
                  waterManeuverId: maneuver.waterManeuverId,
                  success: WorkoutEditing.onlyIfDifferent('success', maneuver, originalManeuver),
                  order: WorkoutEditing.checkOrder(mIndex, maneuver.order || null),
                }

                return WorkoutEditing.noEditingMade(newManeuver) ? null : newManeuver
              }),
              order: WorkoutEditing.checkOrder(index, wave.order || null),
            }

            return WorkoutEditing.noEditingMade(newWave) ? null : newWave
          }) ?? undefined

        await updateWaterActivity(workout.id.toString(), undefined, condition, rpe, time, trimp, waves)
      } else {
        const { rpe, condition, trimp, time } = state

        const waves = state.editableWaves.map(wave => ({
          rightSide: wave.rightSide,
          maneuvers: wave.maneuvers.map(maneuver => ({
            waterManeuverId: maneuver.waterManeuverId,
            success: maneuver.success,
          })),
        }))

        await createWaterActivity(
          athlete.uid.toString(),
          new Date().toISOString().split('T')[0],
          rpe,
          condition,
          trimp,
          time,
          waves
        )
      }
    } catch (error) {
      const msg = handleError(error)
      Alert.alert('Error', 'Failed to save workout. ' + msg)
      return
    }

    Alert.alert('Success', 'Workout saved successfully!', [
      {
        text: 'OK',
        onPress: () => navigation.goBack(),
      },
    ])
  }

  function handleBackPress(changesDone: boolean) {
    if (changesDone) {
      navigation.goBack()
      return
    }

    Alert.alert('Discard changes?', 'Are you sure you want to leave?', [
      { text: 'Cancel', style: 'cancel' },
      { text: 'Save', onPress: handleSave },
      { text: 'Discard', style: 'destructive', onPress: () => navigation.goBack() },
    ])
  }

  function handleOnAddWave() {
    navigation.navigate('Wave', {
      wave: null,
      onSave: (wave: Wave) => {
        dispatch({ type: 'addWave', wave })
        navigation.goBack()
      },
    })
  }

  function handleOnEditWave(wave: Wave) {
    navigation.navigate('Wave', {
      wave: wave,
      onSave: (updatedWave: Wave) => {
        dispatch({ type: 'updateWave', wave: updatedWave })
        navigation.goBack()
      },
    })
  }

  const condition = state.condition
  const rpe = state.rpe
  const time = state.time
  const trimp = state.trimp
  const waves = state.editableWaves

  const disabled =
    time === 0 ||
    (condition === initialState.condition &&
      rpe === initialState.rpe &&
      trimp === initialState.trimp &&
      time === initialState.time &&
      JSON.stringify(waves) === JSON.stringify(initialState.editableWaves))

  return (
    <MainView style={styles.container}>
      <Header title="Back" onBackPress={() => handleBackPress(disabled)} />

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

      <VerticalReorderableList<Wave>
        data={waves}
        onDragEnd={reorderWaves}
        keyExtractor={item => (item.id === null ? item.tempId.toString() : item.id.toString())}
        renderItem={(item, drag, isActive) => (
          <SwipeableItem
            content={
              <>
                <Entypo name={item.rightSide ? 'arrow-long-right' : 'arrow-long-left'} size={24} color="black" />
                <View style={styles.waveContainer}>
                  {item.maneuvers.map((m, index) => (
                    <View key={m.id === null ? m.tempId : m.id} style={styles.maneuverContainer}>
                      <Text>{m.name}</Text>
                      <Ionicons
                        name={m.success ? 'checkmark-circle' : 'close-circle'}
                        size={20}
                        color={m.success ? 'green' : 'red'}
                        style={{ marginLeft: 10 }}
                      />
                      {index < item.maneuvers.length - 1 && <Ionicons name="arrow-forward" size={10} color="black" />}
                    </View>
                  ))}
                </View>
              </>
            }
            onPress={() => handleOnEditWave(item)}
            onLongPress={drag}
            onDelete={() => dispatch({ type: 'removeWave', id: item.id, tempId: item.tempId })}
          />
        )}
        onAdd={handleOnAddWave}
      />
      <View style={styles.footerContainer}>
        <Button text={workout ? 'Save' : 'Add'} onPress={handleSave} disabled={disabled} />
      </View>
    </MainView>
  )
}
