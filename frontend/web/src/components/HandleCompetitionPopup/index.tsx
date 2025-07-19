import * as React from 'react'
import { useReducer } from 'react'
import { useParams } from 'react-router-dom'

import { TextField } from '@mui/material'
import { Popup } from '../Popup'
import { Button } from '../Button'
import { Divisor } from '../Divisor'
import { HandleHeat } from '../HandleHeat'
import { ReorderableList } from '../ReorderableList'

import { Competition } from '../../types/Competition'
import { Heat } from '../../types/Heat'

import { WorkoutEditing } from '../../../../utils/WorkoutEditing'
import { parseToEpoch } from '../../../../utils/parseToEpoch'

import styles from './styles.module.css'

type State =
  | {
      tag: 'editing'
      date: string
      location: string
      place: number
      name: string
      heats: Heat[]
      removedHeats: Heat[]
      error?: string
    }
  | {
      tag: 'submitting'
      date: string
      location: string
      place: number
      name: string
      heats: Heat[]
      removedHeats: Heat[]
    }
  | {
      tag: 'submitted'
    }

type Action =
  | { type: 'edit'; name: string; value: string | number }
  | { type: 'setHeats'; heats: Heat[] }
  | { type: 'addHeat'; heat: Heat }
  | { type: 'removeHeat'; id: number; tempId: number }
  | { type: 'setHeat'; heat: Heat }
  | { type: 'submit' }
  | { type: 'success' }
  | { type: 'error'; error: string }

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'editing':
      switch (action.type) {
        case 'edit':
          return { ...state, [action.name]: action.value }
        case 'setHeats':
          return { ...state, heats: action.heats }
        case 'addHeat':
          return { ...state, heats: [...state.heats, action.heat] }
        case 'removeHeat':
          const removedHeat = state.heats.find(heat => (heat.id === null ? heat.tempId === action.tempId : heat.id === action.id))
          return {
            ...state,
            heats: state.heats.filter(heat => (heat.id === null ? heat.tempId !== action.tempId : heat.id !== action.id)),
            removedHeats: [...state.removedHeats, WorkoutEditing.nullifyFieldsExceptId(removedHeat)],
          }
        case 'setHeat':
          console.log('Setting heat:', action.heat)
          return {
            ...state,
            heats: state.heats.map(heat => {
              const matchById = heat.id !== null && heat.id === action.heat.id
              const matchByTempId = heat.id === null && heat.tempId === action.heat.tempId
              return matchById || matchByTempId ? action.heat : heat
            }),
          }
        case 'submit':
          return { ...state, tag: 'submitting' }
        case 'error':
        default:
          return state
      }

    case 'submitting':
      switch (action.type) {
        case 'success':
          return { tag: 'submitted' }
        case 'error':
          return { ...state, tag: 'editing', error: action.error }
        default:
          return state
      }

    case 'submitted':
      return state
  }
}

type HandleCompetitionPopupProps = {
  competition?: Competition
  onSave: (competition: Competition) => Promise<void>
  onClose: () => void
}

export function HandleCompetitionPopup({ competition, onSave, onClose }: HandleCompetitionPopupProps) {
  const initialState: State = {
    tag: 'editing',
    date: competition ? new Date(competition.date).toISOString().split('T')[0] : '',
    location: competition ? competition.location : '',
    place: competition ? competition.place : 1,
    name: competition ? competition.name : '',
    heats: JSON.parse(JSON.stringify(competition ? competition.heats : [])),
    removedHeats: [],
  }
  const [state, dispatch] = useReducer(reducer, initialState)
  const aid = Number(useParams().aid)

  function handleReorder(heats: Heat[]) {
    dispatch({ type: 'setHeats', heats })
  }

  function handleOnChange(event: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = event.target
    dispatch({ type: 'edit', name, value })
  }

  function handleAddHeat() {
    const heat: Heat = {
      id: null,
      tempId: Date.now(),
      score: 0,
      waterActivity: {
        id: null,
        tempId: Date.now(),
        athleteId: 0,
        date: competition ? competition.date : Date.now(),
        rpe: 5,
        condition: '',
        trimp: 150,
        duration: 1,
        waves: [],
      },
    }
    dispatch({ type: 'addHeat', heat })
  }

  function handleOnSetHeat(heat: Heat) {
    dispatch({ type: 'setHeat', heat })
  }

  function handleOnDeleteHeat(id: number, tempId: number) {
    if (confirm('Are you sure you want to delete this heat?')) {
      dispatch({ type: 'removeHeat', id, tempId })
    }
  }

  async function handleOnSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (state.tag !== 'editing') return

    dispatch({ type: 'submit' })

    const competitionData: Competition = {
      id: competition ? competition.id : null,
      uid: aid,
      name: state.name,
      date: parseToEpoch(state.date, 'yyyy-mm-dd'),
      location: state.location,
      place: state.place,
      heats: state.heats,
    }
    try {
      await onSave(competitionData)
      dispatch({ type: 'success' })
    } catch (error) {
      alert(`Error saving competition: ${error.message}`)
      return
    }
  }

  if (state.tag === 'submitted') return

  const date = state.date
  const location = state.location
  const place = state.place
  const name = state.name
  const heats = state.heats

  const disabled =
    state.tag !== 'editing' ||
    date.length === 0 ||
    location.length === 0 ||
    place <= 0 ||
    name.length === 0 ||
    (date === initialState.date &&
      location === initialState.location &&
      place === initialState.place &&
      name === initialState.name &&
      JSON.stringify(heats) === JSON.stringify(initialState.heats))

  return (
    <Popup
      title={competition ? 'Edit Competition' : 'Add Competition'}
      content={
        <form className={styles.container} onSubmit={handleOnSubmit}>
          <Divisor
            left={
              <div className={styles.details}>
                <TextField
                  name="date"
                  type="date"
                  label="Date"
                  value={date}
                  onChange={handleOnChange}
                  InputLabelProps={{ shrink: true }}
                />
                <TextField name="location" type="text" label="Location" value={location} onChange={handleOnChange} />
                <TextField name="place" type="number" label="Place" value={place} onChange={handleOnChange} />
                <TextField name="name" type="text" label="Name" value={name} onChange={handleOnChange} />
              </div>
            }
            right={
              <div className={styles.heatsContainer}>
                <ReorderableList<Heat>
                  list={heats}
                  renderItem={heat => <HandleHeat key={heat.id} heat={heat} setHeat={handleOnSetHeat} />}
                  onReorder={handleReorder}
                  onDelete={heat => handleOnDeleteHeat(heat.id, heat.tempId)}
                  onAdd={handleAddHeat}
                  cardSize="400px"
                  order="x"
                />
              </div>
            }
          />
          <Button text={competition ? 'Save' : 'Add'} type="submit" disabled={disabled} width="100%" height="30px" />
        </form>
      }
      onClose={onClose}
    />
  )
}
