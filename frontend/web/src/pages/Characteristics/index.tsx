import * as React from 'react'
import { useEffect, useReducer } from 'react'
import { useParams } from 'react-router-dom'

import { CircularProgress, TextField } from '@mui/material'
import { Divisor } from '../../components/Divisor'
import { CharacteristicsChart } from '../../components/CharacteristicsChart'
import { AddCharacteristicsPopup } from '../../components/AddCharacteristicsPopup'
import { ShowSelectedCharacteristicsPopup } from '../../components/ShowSelectedCharacteristicsPopup'
import { Button } from '../../components/Button'
import { Card } from '../../components/Card'

import { CharacteristicsData } from '../../types/CharacterisitcsData'
import { Characteristics } from '../../types/Characteristics'

import { getCharacteristics } from '../../../../services/athleteServices'

import { epochConverter } from '../../../../utils/epochConverter'

import { useAuthentication } from '../../hooks/useAuthentication'

import styles from './styles.module.css'

type State = {
  popupOpen: boolean
  characteristics: Characteristics[]
  selectedCharacteristic: Characteristics
}

type Action =
  | { type: 'togglePopup' }
  | { type: 'toggleShowCharacteristicsPopup'; selectedCharacteristic?: Characteristics }
  | { type: 'setCharacteristics'; characteristics: Characteristics[] }

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'togglePopup':
      return { ...state, popupOpen: !state.popupOpen }
    case 'toggleShowCharacteristicsPopup':
      return {
        ...state,
        selectedCharacteristic: action.selectedCharacteristic,
      }
    case 'setCharacteristics':
      if (action.characteristics.length === 0) return { ...state, characteristics: null }
      else return { ...state, characteristics: action.characteristics }
    default:
      return state
  }
}

const initialState: State = {
  popupOpen: false,
  selectedCharacteristic: undefined,
  characteristics: undefined,
}

export function Characteristics() {
  const [state, dispatch] = useReducer(reducer, initialState)
  const id = useParams().aid
  const [user] = useAuthentication()

  async function fetchCharacteristics() {
    try {
      const res = await getCharacteristics(id)
      dispatch({ type: 'setCharacteristics', characteristics: res.characteristics })
    } catch (error) {
      dispatch({ type: 'setCharacteristics', characteristics: null })
    }
  }

  useEffect(() => {
    fetchCharacteristics()
  }, [])

  function handlePopup() {
    dispatch({ type: 'togglePopup' })
  }

  function handleShowSelectedCharacteristicsPopup(index?: number) {
    if (index === undefined || !state.characteristics[index]) {
      dispatch({ type: 'toggleShowCharacteristicsPopup', selectedCharacteristic: undefined })
      return
    }

    const selectedCharacteristic = {
      ...state.characteristics[index],
      date: state.characteristics[index].date ? epochConverter(Number(state.characteristics[index].date), 'dd-mm-yyyy') : 'N/A',
    }
    dispatch({ type: 'toggleShowCharacteristicsPopup', selectedCharacteristic: selectedCharacteristic })
  }

  const isAddPopupOpen = state.popupOpen
  const selectedCharacteristic = state.selectedCharacteristic

  if (state.characteristics === undefined) return <CircularProgress className={styles.waiting} />

  return (
    <>
      <Divisor
        left={
          <>
            {user.isCoach && <Button text="Add Characteristics" onClick={handlePopup} width="100%" height="50px" />}
            <Card
              content={
                <div className={styles.characteristics}>
                  <h2 className={styles.header}>Last Characteristics</h2>
                  {state.characteristics === null ? (
                    <p>No data available</p>
                  ) : (
                    state.characteristics.slice(-1).map((characteristic, index) => {
                      const entries = Object.entries(characteristic).slice(1)
                      return (
                        <div key={index} className={styles.characteristic}>
                          {entries.map(([key, value]) => (
                            <span key={key}>
                              {key.charAt(0).toUpperCase() + key.slice(1)}:{' '}
                              {key === 'date' ? (value ? epochConverter(Number(value), 'dd-mm-yyyy') : 'N/A') : value || 'N/A'}
                            </span>
                          ))}
                        </div>
                      )
                    })
                  )}
                </div>
              }
              width="100%"
            />
          </>
        }
        right={
          <>
            <Card
              content={
                <CharacteristicsChart
                  labels={
                    state.characteristics
                      ? state.characteristics.map(c => (c?.date ? epochConverter(Number(c.date), 'dd-mm-yyyy') : 'N/A'))
                      : []
                  }
                  data={state.characteristics || []}
                  onPointClick={handleShowSelectedCharacteristicsPopup}
                />
              }
              width="100%"
            />
          </>
        }
      />
      {isAddPopupOpen && <AddCharacteristicsPopup onClose={handlePopup} onSuccess={fetchCharacteristics} />}

      {selectedCharacteristic && (
        <ShowSelectedCharacteristicsPopup
          onClose={handleShowSelectedCharacteristicsPopup}
          onSuccess={fetchCharacteristics}
          data={selectedCharacteristic}
        />
      )}
    </>
  )
}
