import * as React from 'react'
import { createContext, useEffect, useState } from 'react'

import { checkAuth } from '@services/userServices'

import { User } from '@types/User'

import { Storage } from '@storage/Storage'

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

type AuthenticationProviderProps = {
  children: React.ReactNode
}

export function AuthenticationProvider({ children }: AuthenticationProviderProps) {
  const [user, setUser] = useState<User | undefined>(undefined)
  const [loading, setLoading] = useState<boolean>(true)

  useEffect(() => {
    async function fetchUser() {
      try {
        const token = await Storage.getToken()
        if (!token) throw new Error('No token found')
        const { status, res } = await checkAuth(token)
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
