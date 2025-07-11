import * as React from 'react'
import { useReducer, useState } from 'react'
import { TextField } from '@mui/material'
import { Button } from '../../components/Button'
import { Card } from '../../components/Card'

import { updateUsername, updatePassword } from '../../../../services/userServices'

import styles from './styles.module.css'
import { handleError } from '../../../../utils/handleError'

type options = 'changeUsername' | 'changePassword'

export function Account() {
  const [option, setOption] = useState<options>('changeUsername')

  function selectChangeUsername() {
    setOption('changeUsername')
  }

  function selectChangePassword() {
    setOption('changePassword')
  }

  function onSuccess() {
    alert('Operation successful!')
  }

  return (
    <div className={styles.container}>
      <div className={styles.buttons}>
        <Button text="Change Username" onClick={selectChangeUsername} height="30px" />
        <Button text="Change Password" onClick={selectChangePassword} height="30px" />
      </div>
      {option === 'changeUsername' ? <ChangeUsername onSuccess={onSuccess} /> : <ChangePassword onSuccess={onSuccess} />}
    </div>
  )
}

type StateChangeUsername = {
  newUsername: string
  error?: string
}

type ActionChangeUsername = { type: 'edit'; name: string; value: string } | { type: 'error'; error: string }

function changeUsernameReducer(state: StateChangeUsername, action: ActionChangeUsername): StateChangeUsername {
  switch (action.type) {
    case 'edit':
      return { ...state, [action.name]: action.value, error: undefined }
    case 'error':
      return { ...state, error: action.error }
    default:
      return state
  }
}

type ChangeUsernameProps = {
  onSuccess: () => void
}

function ChangeUsername({ onSuccess }: ChangeUsernameProps) {
  const initialState: StateChangeUsername = { newUsername: '' }
  const [state, dispatch] = useReducer(changeUsernameReducer, initialState)

  function onUsernameChange(event: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = event.target
    dispatch({ type: 'edit', name, value })
  }

  async function onUpdateUsername(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()

    try {
      const { newUsername } = state
      await updateUsername(newUsername)
      onSuccess()
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error.res) })
    }
  }

  const { newUsername, error } = state
  const disabled = newUsername.length < 3

  return (
    <div className={styles.container}>
      <Card
        content={
          <>
            <form onSubmit={onUpdateUsername} className={styles.form}>
              <h2 className={styles.title}>Change Username</h2>
              <TextField label="New Username" onChange={onUsernameChange} />
              <Button text="Update Username" type="submit" disabled={disabled} />
              {error && <p className={styles.error}>{error}</p>}
            </form>
          </>
        }
      />
    </div>
  )
}

type StateChangePassword = {
  oldPassword: string
  newPassword: string
  error?: string
}

type ActionChangePassword = { type: 'edit'; name: string; value: string } | { type: 'error'; error: string }

function changePasswordReducer(state: StateChangePassword, action: ActionChangePassword): StateChangePassword {
  switch (action.type) {
    case 'edit':
      return { ...state, [action.name]: action.value, error: undefined }
    case 'error':
      return { ...state, error: action.error }
    default:
      return state
  }
}

type ChangePasswordProps = {
  onSuccess: () => void
}

function ChangePassword({ onSuccess }: ChangePasswordProps) {
  const initialState: StateChangePassword = { oldPassword: '', newPassword: '' }
  const [state, dispatch] = useReducer(changePasswordReducer, initialState)

  function handleOnChange(event: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = event.target
    dispatch({ type: 'edit', name, value })
  }

  async function onUpdatePassword(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()

    try {
      const { oldPassword, newPassword } = state
      await updatePassword(oldPassword, newPassword)
      onSuccess()
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error.res) })
    }
  }

  const { oldPassword, newPassword, error } = state
  const disabled = oldPassword.length < 3 || newPassword.length < 3 || oldPassword === newPassword

  return (
    <div className={styles.container}>
      <Card
        content={
          <>
            <form onSubmit={onUpdatePassword} className={styles.form}>
              <h2 className={styles.title}>Change Password</h2>
              <TextField label="Old Password" name="oldPassword" type="password" value={oldPassword} onChange={handleOnChange} />
              <TextField label="New Password" name="newPassword" type="password" value={newPassword} onChange={handleOnChange} />
              <Button text="Update Password" type="submit" disabled={disabled} />
              {error && <p className={styles.error}>{error}</p>}
            </form>
          </>
        }
      />
    </div>
  )
}
