import { useContext } from 'react'
import { AuthenticationContext } from '../components/AuthenticationProvider'
import { User } from '../types/User'

export function useAuthentication(): [User | undefined, (user: User | undefined) => void, boolean] {
  const state = useContext(AuthenticationContext)

  return [
    state.user,
    (user: User | undefined) => {
      state.setUser(user)
    },
    state.loading,
  ]
}
