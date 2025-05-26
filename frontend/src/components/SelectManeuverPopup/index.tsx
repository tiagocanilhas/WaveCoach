import * as React from 'react'
import { useEffect, useReducer } from 'react'

import { CircularProgress, TextField } from '@mui/material'
import { Popup } from '../Popup'

import { AddNewManeuver } from '../AddNewManeuverPopup'
import { AddManeuverPopup } from '../AddManeuverPopup'
import { ScrollableText } from '../ScrollableText'
import { ObjectList } from '../ObjectList'

import { getWaterManeuvers } from '../../services/waterManeuverServices'

import { WaterManeuver } from '../../types/WaterManeuver'

import styles from './styles.module.css'

type ViewTag = { tag: 'loading' } | { tag: 'choosing' } | { tag: 'creating' } | { tag: 'adding'; maneuver: WaterManeuver }

type State = {
  name: string
  maneuvers?: WaterManeuver[] | undefined
  view: ViewTag
}

type Action =
  | { type: 'choose' }
  | { type: 'new' }
  | { type: 'add'; maneuver: WaterManeuver }
  | { type: 'setData'; maneuvers: WaterManeuver[] }
  | { type: 'setName'; name: string }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'setData':
      return { ...state, maneuvers: action.maneuvers, view: { tag: 'choosing' } }
    case 'choose':
      return { ...state, view: { tag: 'choosing' } }
    case 'new':
      return { ...state, view: { tag: 'creating' } }
    case 'add':
      return { ...state, view: { tag: 'adding', maneuver: action.maneuver } }
    case 'setName':
      return { ...state, name: action.name }
    default:
      return state
  }
}

type SelectManeuverPopupProps = {
  onAdd: (maneuver: WaterManeuver, rightSide: boolean, success: boolean) => void
  onClose: () => void
}

export function SelectManeuverPopup({ onAdd, onClose }: SelectManeuverPopupProps) {
  const initialState: State = { name: '', maneuvers: undefined, view: { tag: 'loading' } }
  const [state, dispatch] = useReducer(reducer, initialState)

  async function fetchData() {
    try {
      const res = await getWaterManeuvers()
      dispatch({ type: 'setData', maneuvers: res.maneuvers })
    } catch (error) {
      console.error('Error fetching maneuvers:', error)
    }
  }

  useEffect(() => {
    fetchData()
  }, [])

  function handleOnChange(e: React.ChangeEvent<HTMLInputElement>) {
    dispatch({ type: 'setName', name: e.target.value })
  }

  function handleOnAdd() {
    dispatch({ type: 'new' })
  }

  function handleOnSelect(maneuver: WaterManeuver) {
    dispatch({ type: 'add', maneuver: maneuver })
  }

  function handleOnClose() {
    dispatch({ type: 'choose' })
  }

  function handleOnSuccess() {
    dispatch({ type: 'choose' })
    fetchData()
  }

  return (
    <>
      <Popup
        title="Select Manuever"
        onClose={onClose}
        content={
          <div className={styles.container}>
            <TextField label="Name" type="text" name="maneuver" onChange={handleOnChange} value={state.name} />
            <div className={styles.addManeuverContainer}>
              {state.view.tag === 'loading' ? (
                <CircularProgress />
              ) : (
                <ObjectList
                  items={state.maneuvers.filter(e => e.name.toLowerCase().includes(state.name.toLowerCase()))}
                  getKey={item => item.id}
                  onItemClick={handleOnSelect}
                  renderItem={item => (
                    <div className={styles.maneuver}>
                      <img src={item.url || `/images/no_image.svg`} alt="Exercise" />
                      <ScrollableText text={item.name} className={styles.maneuverName} />
                    </div>
                  )}
                  onAdd={handleOnAdd}
                />
              )}
            </div>
          </div>
        }
      />

      {state.view.tag === 'creating' && <AddNewManeuver onClose={handleOnClose} onSuccess={handleOnSuccess} />}
      {state.view.tag === 'adding' && <AddManeuverPopup maneuver={state.view.maneuver} onAdd={onAdd} onClose={handleOnClose} />}
    </>
  )
}
