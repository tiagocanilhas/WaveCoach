import * as React from 'react'
import { useEffect, useReducer } from 'react'
import { useParams } from 'react-router-dom'

import { CircularProgress } from '@mui/material'
import { ObjectList } from '../../components/ObjectList'
import { CompetitionCard } from '../../components/CompetitionCard'

import { getCompetitions } from '../../../../services/athleteServices'

import { Competition } from '../../types/Competition'

import { handleError } from '../../../../utils/handleError'

import styles from './styles.module.css'
import { CompetitionPopup } from '../../components/CompetitionPopup'
import { AddCompetitionPopup } from '../../components/AddCompetitionPopup'

type State =
  | { tag: 'loading' }
  | { tag: 'loaded'; competitions: Competition[]; competition?: Competition; isAdding: boolean }
  | { tag: 'error'; error: string }

type Action =
  | { type: 'setCompetitions'; competitions: Competition[] }
  | { type: 'setCompetition'; competition: Competition }
  | { type: 'toggleIsAdding' }
  | { type: 'error'; error: string }

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'loading':
      switch (action.type) {
        case 'setCompetitions':
          return { tag: 'loaded', competitions: action.competitions, isAdding: false }
        case 'error':
          return { tag: 'error', error: action.error }
        default:
          return state
      }

    case 'loaded':
      switch (action.type) {
        case 'setCompetitions':
          return { ...state, competitions: action.competitions }
        case 'setCompetition':
          return { ...state, competition: action.competition }
        case 'toggleIsAdding':
          return { ...state, isAdding: !state.isAdding }
        case 'error':
          return { tag: 'error', error: action.error }
        default:
          return state
      }

    case 'error':
      return state
  }
}

export function Competition() {
  const initialState: State = { tag: 'loading' }
  const [state, dispatch] = useReducer(reducer, initialState)
  const aid = Number(useParams().aid)

  async function fetchCompetitions() {
    try {
      const { res } = await getCompetitions(aid)
      dispatch({ type: 'setCompetitions', competitions: res.competitions })
    } catch (error) {
      dispatch({ type: 'error', error: handleError(error.res) })
    }
  }

  useEffect(() => {
    fetchCompetitions()
  }, [])

  if (state.tag === 'loading') return <CircularProgress />

  if (state.tag === 'error') return <div>Error: {state.error}</div>

  async function handleOnDeleteSuccess() {
    await fetchCompetitions()
  }

  function handleOnClick(competition: Competition) {
    dispatch({ type: 'setCompetition', competition })
  }

  function handleToggleIsAdding() {
    dispatch({ type: 'toggleIsAdding' })
  }

  async function handleOnSuccess() {
    await fetchCompetitions()
    dispatch({ type: 'toggleIsAdding' })
  }
  
  async function handleOnUpdateSuccess() {
    await fetchCompetitions()
    dispatch({ type: 'setCompetition', competition: undefined })
  }

  const competitions = state.competitions
  const competition = state.competition
  const isAdding = state.isAdding

  return (
    <div className={styles.container}>
      <ObjectList
        items={competitions}
        getKey={item => item.id}
        renderItem={item => (
          <CompetitionCard competition={item} onDeleteSuccess={handleOnDeleteSuccess} onClick={handleOnClick} />
        )}
        onAdd={handleToggleIsAdding}
        cardSize="400px"
      />

      {isAdding && <AddCompetitionPopup onClose={handleToggleIsAdding} onSuccess={handleOnSuccess} />}

      {competition && <CompetitionPopup competition={competition} onClose={() => handleOnClick(undefined)} onUpdateSuccess={handleOnUpdateSuccess} />}
    </div>
  )
}
