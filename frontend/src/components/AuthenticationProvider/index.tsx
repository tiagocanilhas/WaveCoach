import * as React from 'react'
import { createContext, useEffect, useState } from 'react'

import { checkAuth } from '../../services/userServices'

import { User } from '../../types/User'

type AuthenticationContextType = {
  user: User | undefined
  setUser: React.Dispatch<React.SetStateAction<User | undefined>>
  loading: boolean
}

export const AuthenticationContext = createContext<AuthenticationContextType>({
  user: undefined,
  setUser: () => {},
  loading: true,
})

export function AuthenticationProvider({ children }) {
  const [user, setUser] = useState<User | undefined>(undefined)
  const [loading, setLoading] = useState<boolean>(true)

  useEffect(() => {
    async function fetchUser() {
      try {
        const res = await checkAuth()
        setUser(res)
      } catch (err) {
        setUser(undefined)
      } finally {
        setLoading(false)
      }
    }

    fetchUser()
  }, [])

  const value = {
    user,
    setUser,
    loading,
  }

  return <AuthenticationContext.Provider value={value}>{children}</AuthenticationContext.Provider>
}
