import { useContext } from 'react'
import { AuthenticationContext } from '../components/AuthenticationProvider'
import { User } from '../types/user'

export function useAuthentication(): [User | undefined, (user: User) => void, boolean] {
  const state = useContext(AuthenticationContext)

  return [
    state.user,
    (user: User) => {
      state.setUser(user)
    },
    state.loading,
  ]
}
