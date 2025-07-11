import * as React from 'react'
import { useReducer } from 'react'
import { Text } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'

import { MainView } from '@components/MainView'
import { VerticalReorderableList } from '@components/VerticalReorderableList'
import { NavigationProp, useNavigation } from '@react-navigation/native'
import { RootStackParamList } from '@components/Navigator'
import { ManeuverPopup } from '@components/ManeuverPopup'
import { SwipeableItem } from '@components/SwipeableItem'
import { Switch } from '@components/Switch'
import { Button } from '@components/Button'

import { Maneuver } from '@types/Maneuver'

import { WorkoutEditing } from '@utils/WorkoutEditing'

import { styles } from './styles'

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

type WaveProps = {
  editing?: boolean
  rightSide: boolean
  maneuvers: Maneuver[]
  onSave: (rightSide: boolean, maneuvers: Maneuver[]) => void
}

export function Wave({ editing, rightSide, maneuvers, onSave }: WaveProps) {
  const { editable, removed } = WorkoutEditing.separateList(maneuvers)
  const initialState: State = {
    rightSide,
    editableManeuvers: editable,
    removedManeuvers: removed,
    maneuverToEdit: null,
  }
  const [state, dispatch] = useReducer(reducer, initialState)
  const navigation = useNavigation<NavigationProp<RootStackParamList, 'Wave'>>()

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
    onSave(state.rightSide, [...state.editableManeuvers, ...state.removedManeuvers])
  }

  return (
    <>
      <MainView style={styles.container}>
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

        <Button text={editing ? 'Save' : 'Add'} onPress={handleOnSave} disabled={state.editableManeuvers.length === 0} />
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
