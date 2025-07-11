import * as React from 'react'
import { StatusBar } from 'react-native'
import { NavigationContainer } from '@react-navigation/native'
import { createNativeStackNavigator } from '@react-navigation/native-stack'
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs'
import Ionicons from '@expo/vector-icons/Ionicons'

import { Login } from '@screens/Login'
import { Home } from '@screens/Home'
import { WaterWorkout } from '@screens/WaterWorkout'
import { Settings } from '@screens/Settings'
import { Wave } from '@screens/Wave'
import { Maneuvers } from '@screens/Maneuvers'

import { Loading } from '@components/Loading'

import { useAuthentication } from '@hooks/useAuthentication'

import { Athlete } from '@types/Athlete'
import { WaterWorkout as WaterWorkoutType } from '@types/WaterWorkout'
import { Wave as WaveType } from '@types/Wave'
import { Maneuver as ManeuverType } from '@types/Maneuver'

import { styles } from './styles'

export type RootStackParamList = {
  Login: undefined
  Home: undefined
  TabNavigator: undefined
  Settings: undefined
  WaterWorkout: { athlete: Athlete; workout: WaterWorkoutType | null }
  Wave: { wave: WaveType | null; onSave: (wave: WaveType) => void }
  Maneuvers: { onSave: (maneuver: ManeuverType) => void }
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

function AuthenticatedStackNavigator() {
  return (
    <Stack.Navigator initialRouteName="Home">
      <Stack.Screen name="Home" component={TabNavigator} options={{ headerShown: false }} />
      <Stack.Screen name="WaterWorkout" component={WaterWorkout} options={{ headerShown: false, gestureEnabled: false }} />
      <Stack.Screen name="Wave" component={Wave} options={{ headerShown: false }} />
      <Stack.Screen name="Maneuvers" component={Maneuvers} options={{ headerShown: false }} />
    </Stack.Navigator>
  )
}

function UnauthenticatedStackNavigator() {
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
      {user ? <AuthenticatedStackNavigator /> : <UnauthenticatedStackNavigator />}
    </NavigationContainer>
  )
}
