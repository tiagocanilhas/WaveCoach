import * as React from 'react'
import { useReducer } from 'react'
import { Text, View } from 'react-native'
import { NavigationProp, useNavigation } from '@react-navigation/native'

import { RootStackParamList } from '@components/Navigator'
import { Input } from '@components/Input'
import { Button } from '@components/Button'
import { MainView } from '@components/MainView'

import { useAuthentication } from '@hooks/useAuthentication'

import { login } from '@services/userServices'

import { handleError } from '@utils/handleError'

import { Storage } from '@storage/Storage'

import { styles } from './styles'

type State =
  | { tag: 'editing'; error?: string; inputs: { username: string; password: string } }
  | { tag: 'submitting'; username: string }
  | { tag: 'redirecting' }

type Action =
  | { type: 'edit'; name: string; value: string }
  | { type: 'submit' }
  | { type: 'error'; error: string }
  | { type: 'success' }

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'editing':
      switch (action.type) {
        case 'edit':
          return { tag: 'editing', error: undefined, inputs: { ...state.inputs, [action.name]: action.value } }
        case 'submit':
          return { tag: 'submitting', username: state.inputs.username }
        default:
          return state
      }

    case 'submitting':
      switch (action.type) {
        case 'error':
          return { tag: 'editing', error: action.error, inputs: { username: state.username, password: '' } }
        case 'success':
          return { tag: 'redirecting' }
        default:
          return state
      }

    case 'redirecting':
      return state
  }
}

export function Login() {
  const initialState: State = { tag: 'editing', inputs: { username: '', password: '' } }
  const [state, dispatch] = useReducer(reducer, initialState)
  const navigation = useNavigation<NavigationProp<RootStackParamList, 'Login'>>()
  const [user, setUser] = useAuthentication()

  function handleInputChange(name: string, value: string) {
    dispatch({ type: 'edit', name, value })
  }

  async function handleOnSubmit() {
    if (state.tag !== 'editing') return

    dispatch({ type: 'submit' })

    const name = state.inputs.username.trim()
    const password = state.inputs.password.trim()

    try {
      const { status, res } = await login(name, password)

      if (!res.isCoach) throw { status, res: { title: 'Athlete cannot login', type: '' } }

      setUser({ id: res.id, username: res.username })
      await Storage.saveToken(res.token)

      dispatch({ type: 'success' })
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error.res) })
    }
  }

  if (state.tag === 'redirecting') {
    navigation.navigate('Home')
    return null
  }

  const username = state.tag === 'submitting' ? state.username : state.inputs.username
  const password = state.tag === 'submitting' ? '' : state.inputs.password
  const error = state.tag === 'submitting' ? undefined : state.error
  const disabled = state.tag === 'submitting' || state.inputs.username.trim() === '' || state.inputs.password.trim() === ''

  return (
    <MainView>
      <Text style={styles.title}>Login</Text>
      <View style={styles.inputContainer}>
        <Input
          style={styles.input}
          placeholder="Username"
          value={username}
          onChange={text => handleInputChange('username', text)}
        />
        <Input
          style={styles.input}
          placeholder="Password"
          secureTextEntry
          value={password}
          onChange={text => handleInputChange('password', text)}
        />
      </View>
      <Button text="Login" onPress={handleOnSubmit} disabled={disabled} />
      {error && <Text style={styles.error}>{error}</Text>}
    </MainView>
  )
}
