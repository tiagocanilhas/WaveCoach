import * as React from 'react'
import { useReducer } from 'react'
import { Alert, Text } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'

import { MainView } from '@components/MainView'
import { VerticalReorderableList } from '@components/VerticalReorderableList'
import { NavigationProp, RouteProp, useNavigation, useRoute } from '@react-navigation/native'
import { RootStackParamList } from '@components/Navigator'
import { ManeuverPopup } from '@components/ManeuverPopup'
import { SwipeableItem } from '@components/SwipeableItem'
import { Switch } from '@components/Switch'
import { Button } from '@components/Button'

import { Maneuver } from '@types/Maneuver'

import { WorkoutEditing } from '@utils/WorkoutEditing'

import { styles } from './styles'
import { Header } from '@components/Header'

type State = {
  rightSide: boolean
  editableManeuvers: Maneuver[]
  removedManeuvers: Maneuver[]
  maneuverToEdit: Maneuver | null
}

type Action =
  | { type: 'toggleSide' }
  | { type: 'addManeuver'; maneuver: Maneuver }
  | { type: 'setManeuverToEdit'; maneuver: Maneuver | null }
  | { type: 'updateManeuver'; maneuver: Maneuver }
  | { type: 'deleteManeuver'; id: number | null; tempId?: number }
  | { type: 'setManeuvers'; maneuvers: Maneuver[] }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'toggleSide':
      return { ...state, rightSide: !state.rightSide }
    case 'addManeuver':
      return {
        ...state,
        editableManeuvers: [...state.editableManeuvers, action.maneuver],
      }
    case 'setManeuverToEdit':
      return { ...state, maneuverToEdit: action.maneuver }
    case 'updateManeuver':
      return {
        ...state,
        editableManeuvers: state.editableManeuvers.map(maneuver => {
          const matchById = maneuver.id !== null && maneuver.id === action.maneuver.id
          const matchByTempId = maneuver.id === null && maneuver.tempId === action.maneuver.tempId
          return matchById || matchByTempId ? action.maneuver : maneuver
        }),
        maneuverToEdit: null,
      }
    case 'deleteManeuver':
      const removedManeuver = state.editableManeuvers.find(maneuver =>
        maneuver.id === null ? maneuver.tempId === action.tempId : maneuver.id === action.id
      )
      if (!removedManeuver) return state

      return {
        ...state,
        editableManeuvers: state.editableManeuvers.filter(maneuver => maneuver !== removedManeuver),
        removedManeuvers: [...state.removedManeuvers, WorkoutEditing.nullifyFieldsExceptId(removedManeuver)],
      }
    case 'setManeuvers':
      return {
        ...state,
        editableManeuvers: action.maneuvers,
      }
    default:
      return state
  }
}

export function Wave() {
  const navigation = useNavigation<NavigationProp<RootStackParamList, 'Wave'>>()
  const route = useRoute<RouteProp<RootStackParamList, 'Wave'>>()
  const { wave, onSave } = route.params

  const { editable, removed } = WorkoutEditing.separateList(wave?.maneuvers || [])
  const initialState: State = {
    rightSide: wave?.rightSide || false,
    editableManeuvers: editable,
    removedManeuvers: removed,
    maneuverToEdit: null,
  }
  const [state, dispatch] = useReducer(reducer, initialState)

  function handleOnToggleSide() {
    dispatch({ type: 'toggleSide' })
  }

  function reoderManeuvers(maneuvers: Maneuver[]) {
    dispatch({ type: 'setManeuvers', maneuvers })
  }

  function handleOnAddManeuver() {
    navigation.navigate('Maneuvers', {
      onSave: (maneuver: Maneuver) => {
        dispatch({ type: 'addManeuver', maneuver: { ...maneuver, order: state.editableManeuvers.length + 1 } })
      },
    })
  }

  function handleSetManeuverToEdit(maneuver: Maneuver | null) {
    dispatch({ type: 'setManeuverToEdit', maneuver })
  }

  function handleOnUpdateManeuver(success: boolean) {
    dispatch({ type: 'updateManeuver', maneuver: { ...state.maneuverToEdit, success } as Maneuver })
  }

  function handleOnDeleteManeuver(id: number | null, tempId?: number) {
    dispatch({ type: 'deleteManeuver', id, tempId })
  }

  function handleOnSave() {
    onSave({
      id: wave?.id || null,
      tempId: wave?.tempId || new Date().getTime(),
      rightSide: state.rightSide,
      maneuvers: [...state.editableManeuvers, ...state.removedManeuvers],
    })
  }

  function handleBackPress(changesDone: boolean) {
    if (changesDone) {
      navigation.goBack()
      return
    }

    Alert.alert('Discard changes?', 'Are you sure you want to leave?', [
      { text: 'Cancel', style: 'cancel' },
      { text: 'Save', onPress: handleOnSave },
      { text: 'Discard', style: 'destructive', onPress: () => navigation.goBack() },
    ])
  }

  const disabled = state.editableManeuvers === initialState.editableManeuvers

  return (
    <>
      <MainView>
        <Header title="Back" onBackPress={() => handleBackPress(disabled)} />
        <Switch onChange={handleOnToggleSide} value={state.rightSide} leftLabel="Left Side" rightLabel="Right Side" />
        <VerticalReorderableList<Maneuver>
          data={state.editableManeuvers}
          onDragEnd={reoderManeuvers}
          keyExtractor={item => JSON.stringify(item)}
          renderItem={(item, drag, isActive) => (
            <SwipeableItem
              content={
                <>
                  <Text>{item.name} </Text>
                  <Ionicons
                    name={item.success ? 'checkmark-circle' : 'close-circle'}
                    size={20}
                    color={item.success ? 'green' : 'red'}
                    style={{ marginLeft: 10 }}
                  />
                </>
              }
              onPress={() => handleSetManeuverToEdit(item)}
              onLongPress={drag}
              onDelete={() => handleOnDeleteManeuver(item.id, item.tempId)}
            />
          )}
          onAdd={handleOnAddManeuver}
        />
        <Button text={wave ? 'Save' : 'Add'} onPress={handleOnSave} disabled={disabled} />
      </MainView>

      {state.maneuverToEdit && (
        <ManeuverPopup
          title={state.maneuverToEdit.name}
          maneuver={state.maneuverToEdit}
          onSave={handleOnUpdateManeuver}
          onClose={() => handleSetManeuverToEdit(null)}
        />
      )}
    </>
  )
}
