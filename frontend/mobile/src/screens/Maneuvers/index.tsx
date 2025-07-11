import * as React from 'react'
import { useEffect, useReducer } from 'react'
import { Text, View } from 'react-native'
import { NavigationProp, RouteProp, useNavigation, useRoute } from '@react-navigation/native'

import { MainView } from '@components/MainView'
import { WaterManeuver } from '@components/WaterManeuver'
import { Loading } from '@components/Loading'
import { ItemsWithSearchBox } from '@components/ItemsWithSearchBox'
import { ManeuverPopup } from '@components/ManeuverPopup'
import { RootStackParamList } from '@components/Navigator'

import { getWaterManeuvers } from '@services/waterManeuverServices'

import { WaterManeuver as WaterManeuverType } from '@types/WaterManeuver'

import { styles } from './styles'
import { Header } from '@components/Header'

type State =
  | { tag: 'loading' }
  | { tag: 'loaded'; maneuvers: WaterManeuverType[]; maneuver: WaterManeuverType | null; searchName: string }
  | { tag: 'error'; error: string }

type Action =
  | { type: 'setManeuvers'; maneuvers: WaterManeuverType[] }
  | { type: 'setSearchName'; searchName: string }
  | { type: 'selectManeuver'; maneuver: WaterManeuverType | null }
  | { type: 'error'; error: string }

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'loading':
      switch (action.type) {
        case 'setManeuvers':
          return { tag: 'loaded', maneuvers: action.maneuvers, maneuver: null, searchName: '' }
        case 'error':
          return { tag: 'error', error: action.error }
        default:
          return state
      }
    case 'loaded':
      switch (action.type) {
        case 'setSearchName':
          return { ...state, searchName: action.searchName }
        case 'selectManeuver':
          return { ...state, maneuver: action.maneuver }
        default:
          return state
      }
    case 'error':
      return state
  }
}

export function Maneuvers() {
  const [state, dispatch] = useReducer(reducer, { tag: 'loading' })
  const navigation = useNavigation<NavigationProp<RootStackParamList, 'Maneuvers'>>()
  const route = useRoute<RouteProp<RootStackParamList, 'Maneuvers'>>()
  const { onSave } = route.params

  useEffect(() => {
    async function fetchData() {
      try {
        const { status, res } = await getWaterManeuvers()
        dispatch({ type: 'setManeuvers', maneuvers: res.maneuvers })
      } catch (error) {
        console.error('Error fetching maneuvers:', error)
      }
    }
    fetchData()
  }, [])

  if (state.tag === 'error')
    return (
      <MainView style={styles.container}>
        <Text>Error: {state.error}</Text>
      </MainView>
    )

  function handleSelectManeuver(maneuver: WaterManeuverType | null) {
    dispatch({ type: 'selectManeuver', maneuver })
  }

  function handleOnChange(value: string) {
    dispatch({ type: 'setSearchName', searchName: value })
  }

  function handleOnSave(success: boolean) {
    if (onSave && state.tag === 'loaded' && state.maneuver) {
      onSave({
        id: null,
        tempId: new Date().getTime(),
        waterManeuverId: state.maneuver.id,
        name: state.maneuver.name,
        url: state.maneuver.url,
        success: success,
      })
    }

    navigation.goBack()
  }

  function handleBackPress() {
    navigation.goBack()
  }

  const value = state.tag === 'loaded' ? state.searchName : ''
  const maneuvers = state.tag === 'loaded' ? state.maneuvers : []
  const maneuver = state.tag === 'loaded' ? state.maneuver : null

  return (
    <>
      <MainView style={styles.container}>
        <Header title="Back" onBackPress={handleBackPress} />
        {state.tag === 'loading' ? (
          <Loading />
        ) : (
          <ItemsWithSearchBox<WaterManeuverType>
            nameProperty="name"
            value={value}
            onChange={handleOnChange}
            items={maneuvers}
            renderItem={(maneuver: WaterManeuverType) => (
              <WaterManeuver key={maneuver.id} maneuver={maneuver} onPress={handleSelectManeuver} />
            )}
          />
        )}
      </MainView>

      {maneuver && <ManeuverPopup title={maneuver.name} onSave={handleOnSave} onClose={() => handleSelectManeuver(null)} />}
    </>
  )
}
