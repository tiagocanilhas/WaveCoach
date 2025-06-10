import * as React from 'react'
import { StatusBar } from 'react-native'
import { NavigationContainer } from '@react-navigation/native'
import { createNativeStackNavigator } from '@react-navigation/native-stack'
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs'
import Ionicons from '@expo/vector-icons/Ionicons'

import { Login } from '@screens/Login'
import { Home } from '@screens/Home'
import { AddingWaterWorkout } from '@screens/AddingWaterWorkout'
import { Settings } from '@screens/Settings'

import { Loading } from '@components/Loading'

import { useAuthentication } from '@hooks/useAuthentication'

import { Athlete } from '@types/Athlete'

import { styles } from './styles'

export type RootStackParamList = {
  Login: undefined
  Home: undefined
  TabNavigator: undefined
  Settings: undefined
  AddingWaterWorkout: { athlete: Athlete }
}

const Tab = createBottomTabNavigator()
const Stack = createNativeStackNavigator<RootStackParamList>()

function TabNavigator() {
  return (
    <Tab.Navigator screenOptions={styles.tabBarStyle}>
      <Tab.Screen
        name="Home"
        component={Home}
        options={{
          headerShown: false,
          tabBarIcon: ({ size, focused }) => (
            <Ionicons
              name={focused ? 'home' : 'home-outline'}
              size={size}
              color={focused ? styles.focusedColor : styles.unfocusedColor}
            />
          ),
        }}
      />
      <Tab.Screen
        name="Settings"
        component={Settings}
        options={{
          headerShown: false,
          tabBarIcon: ({ size, focused }) => (
            <Ionicons
              name={focused ? 'settings' : 'settings-outline'}
              size={size}
              color={focused ? styles.focusedColor : styles.unfocusedColor}
            />
          ),
        }}
      />
    </Tab.Navigator>
  )
}

function AuthenticatedStack() {
  return (
    <Stack.Navigator initialRouteName="Home">
      <Stack.Screen name="Home" component={TabNavigator} options={{ headerShown: false }} />
      <Stack.Screen name="AddingWaterWorkout" component={AddingWaterWorkout} options={{ headerShown: false }} />
    </Stack.Navigator>
  )
}

function UnauthenticatedStack() {
  return (
    <Stack.Navigator initialRouteName="Login">
      <Stack.Screen name="Login" component={Login} options={{ headerShown: false }} />
    </Stack.Navigator>
  )
}

export function Navigator() {
  const [user, _, loading] = useAuthentication()

  if (loading) return <Loading />

  return (
    <NavigationContainer>
      <StatusBar />
      {user ? <AuthenticatedStack /> : <UnauthenticatedStack />}
    </NavigationContainer>
  )
}
