import * as React from 'react'
import { useEffect, useReducer, useCallback } from 'react'
import { NavigationProp, useNavigation } from '@react-navigation/native'
import { Text, Alert, BackHandler } from 'react-native'
import { useFocusEffect } from '@react-navigation/native'

import { RootStackParamList } from '@components/Navigator'
import { MainView } from '@components/MainView'
import { Athlete } from '@components/Athlete'
import { Loading } from '@components/Loading'
import { AthleteExtended } from '@components/AthleteExtended'
import { ItemsWithSearchBox } from '@components/ItemsWithSearchBox'

import { useAuthentication } from '@hooks/useAuthentication'

import { getAthletes } from '@services/athleteServices'
import { logout } from '@services/userServices'

import { Athlete as AthleteType } from '@types/Athlete'

import { getLastWaterActivity } from '@services/athleteServices'

import { handleError } from '@utils/handleError'

import { Storage } from '@storage/Storage'

import { styles } from './styles'

type State = { athletes: AthleteType[] | null | undefined; isExtended: boolean; name: string; error?: string }

type Action =
  | { type: 'toggleExtended' }
  | { type: 'error'; error?: string }
  | { type: 'success'; athletes: AthleteType[] }
  | { type: 'edit'; name: string }

export function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'toggleExtended':
      return { ...state, isExtended: !state.isExtended }
    case 'error':
      return { ...state, error: action.error }
    case 'success':
      return { ...state, athletes: action.athletes, name: '' }
    case 'edit':
      return { ...state, name: action.name }
    default:
      return state
  }
}

export function Home() {
  const initialState: State = { athletes: undefined, isExtended: false, name: '', error: undefined }
  const [state, dispatch] = useReducer(reducer, initialState)
  const navigation = useNavigation<NavigationProp<RootStackParamList, 'Home'>>()
  const [user, setUser] = useAuthentication()

  useEffect(() => {
    async function fetchAthletes() {
      try {
        const token = await Storage.getToken()
        const { status, res } = await getAthletes(token ?? '')
        dispatch({ type: 'success', athletes: res.athletes })
      } catch (error) {
        dispatch({ type: 'error', error: 'Failed to fetch athletes' })
      }
    }
    fetchAthletes()
  }, [])

  useFocusEffect(
    useCallback(() => {
      function onBackPress() {
        Alert.alert(
          'Leaving Application',
          'Do you want to logout or exit the application?',
          [
            {
              text: 'Cancel',
              style: 'cancel',
              onPress: () => null,
            },
            {
              text: 'Logout',
              style: 'destructive',
              onPress: async () => {
                await logout()
                setUser(undefined)
              },
            },
            {
              text: 'Exit',
              onPress: () => BackHandler.exitApp(),
            },
          ],
          { cancelable: true }
        )
        return true
      }

      const backHandler = BackHandler.addEventListener('hardwareBackPress', onBackPress)
      return () => backHandler.remove()
    }, [navigation])
  )

  function handleInputChange(name: string) {
    dispatch({ type: 'edit', name })
  }

  function toggleExtended() {
    dispatch({ type: 'toggleExtended' })
  }

  function select(athlete: AthleteType) {
    Alert.alert(
      'Add workout',
      'Do you want to edit the last workout or create a new one?',
      [
        {
          text: 'Edit last',
          onPress: () => editWaterWorkout(athlete),
        },
        {
          text: 'Create new',
          onPress: () => createWaterWorkout(athlete),
        },
        {
          text: 'Cancel',
          style: 'cancel',
          onPress: () => null,
        },
      ],
      { cancelable: true }
    )
  }

  async function createWaterWorkout(athlete: AthleteType) {
    navigation.navigate('WaterWorkout', { athlete, workout: null })
    await new Promise(resolve => setTimeout(resolve, 600))
    dispatch({ type: 'edit', name: '' })
  }

  async function editWaterWorkout(athlete: AthleteType) {
    try {
      const { status, res } = await getLastWaterActivity(athlete.uid.toString())
      const workout = {
        id: res.id,
        rpe: res.rpe,
        trimp: res.trimp,
        condition: res.condition,
        time: res.duration,
        waves: res.waves.map(wave => ({
          id: wave.id,
          rightSide: wave.rightSide,
          maneuvers: wave.maneuvers.map(maneuver => ({
            id: maneuver.id,
            waterManeuverId: maneuver.waterManeuverId,
            name: maneuver.name,
            url: maneuver.url,
            success: maneuver.success,
          })),
        })),
      }
      navigation.navigate('WaterWorkout', { athlete, workout })
      await new Promise(resolve => setTimeout(resolve, 600))
      dispatch({ type: 'edit', name: '' })
    } catch (error) {
      Alert.alert('Error', `Failed to fetch last workout: ${handleError(error)}`)
    }
  }

  const searchName = state.name
  const isExtended = state.isExtended
  const athletes = state.athletes
  const error = state.error

  if (!athletes)
    return (
      <MainView style={styles.container}>
        <Loading />
      </MainView>
    )

  if (error)
    return (
      <MainView style={styles.container}>
        <Text style={styles.title}>Error: {error}</Text>
      </MainView>
    )

  if (athletes.length === 0)
    return (
      <MainView style={styles.container}>
        <Text style={styles.title}>No athletes found</Text>
      </MainView>
    )

  return (
    <ItemsWithSearchBox<AthleteType>
      nameProperty="name"
      value={searchName}
      onChange={handleInputChange}
      items={athletes}
      isExtended={isExtended}
      toggleExtended={toggleExtended}
      renderItem={(athlete: AthleteType) => {
        const AthleteComponent = isExtended ? AthleteExtended : Athlete
        return <AthleteComponent athlete={athlete} key={athlete.uid} onPress={select} />
      }}
    />
  )
}
