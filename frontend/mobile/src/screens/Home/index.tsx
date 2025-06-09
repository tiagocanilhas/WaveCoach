import * as React from 'react'
import { useEffect, useReducer, useCallback } from 'react'
import { NavigationProp, useNavigation } from '@react-navigation/native'
import { Text, ScrollView, View, Alert, BackHandler, TouchableOpacity } from 'react-native'
import { useFocusEffect } from '@react-navigation/native'
import { MaterialIcons } from '@expo/vector-icons'

import { RootStackParamList } from '@components/Navigator'
import { MainView } from '@components/MainView'
import { Athlete } from '@components/Athlete'
import { Input } from '@components/Input'
import { Loading } from '@components/Loading'

import { useAuthentication } from '@hooks/useAuthentication'

import { getAthletes } from '@services/athleteServices'
import { logout } from '@services/userServices'

import { Athlete as AthleteType } from '@types/Athlete'

import { Storage } from '@storage/Storage'

import { styles } from './styles'
import { AthleteExtended } from '@components/AthleteExtended'

type State = {
  athletes: AthleteType[] | null | undefined
  isExtended: boolean
  name: string
  error?: string
}

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
        const res = await getAthletes(token ?? '')
        dispatch({ type: 'success', athletes: res.athletes })
      } catch (error) {
        dispatch({ type: 'error', error: 'Failed to fetch athletes' })
      }
    }
    fetchAthletes()
  }, [])

  useFocusEffect(
    useCallback(() => {
      const onBackPress = () => {
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

  async function createWaterWorkout(athlete: AthleteType) {
    navigation.navigate('AddingWaterWorkout', { athlete })
    await new Promise(resolve => setTimeout(resolve, 600))
    dispatch({ type: 'edit', name: '' })
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

  const filteredAthletes = athletes.filter(a => a.name.toLowerCase().includes(searchName.toLowerCase()))

  return (
    <MainView style={styles.container}>
      <View style={styles.header}>
        <Input style={styles.input} value={searchName} placeholder="Search" onChange={handleInputChange} />
        <TouchableOpacity style={styles.button} onPress={toggleExtended}>
          <MaterialIcons name={isExtended ? 'view-agenda' : 'view-module'} size={24} color="black" />
        </TouchableOpacity>
      </View>
      <ScrollView style={styles.scrollContainer}>
        <View style={[styles.athletesContainer, isExtended ? styles.extended : styles.notExtended]}>
          {filteredAthletes.map(athlete => {
            const AthleteComponent = isExtended ? AthleteExtended : Athlete
            return <AthleteComponent athlete={athlete} key={athlete.uid} onPress={createWaterWorkout} />
          })}
        </View>
      </ScrollView>
    </MainView>
  )
}
