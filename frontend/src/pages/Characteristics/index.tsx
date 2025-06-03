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

import { getCharacteristics } from '../../../services/athleteServices'

import { epochConverter } from '../../../utils/epochConverter'
import { handleError } from '../../../utils/handleError'

import { useAuthentication } from '../../hooks/useAuthentication'

import styles from './styles.module.css'

type State = {
  popupOpen: boolean
  selectedCharacteristicpopupOpen: boolean
  characteristics: Characteristics[]
  selectedCharacteristic?: Characteristics
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
        selectedCharacteristicpopupOpen: !state.selectedCharacteristicpopupOpen,
        selectedCharacteristic: action.selectedCharacteristic,
      }
    case 'setCharacteristics':
      return { ...state, characteristics: action.characteristics }
    default:
      return state
  }
}

const initialState: State = {
  popupOpen: false,
  selectedCharacteristicpopupOpen: false,
  characteristics: [],
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
      dispatch({ type: 'setCharacteristics', characteristics: [] })
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

  const characteristicsData: CharacteristicsData[] = [
    {
      label: 'Height',
      data: state.characteristics.map(characteristic => characteristic.height),
      backgroundColor: 'rgb(192, 147, 75)',
      borderColor: 'rgb(192, 147, 75)',
    },
    {
      label: 'Weight',
      data: state.characteristics.map(characteristic => characteristic.weight),
      backgroundColor: 'rgb(153, 102, 255)',
      borderColor: 'rgb(153, 102, 255)',
    },
    {
      label: 'Calories',
      data: state.characteristics.map(characteristic => characteristic.calories),
      backgroundColor: 'rgb(75, 192, 192)',
      borderColor: 'rgb(75, 192, 192)',
    },
    {
      label: 'Body Fat',
      data: state.characteristics.map(characteristic => characteristic.bodyFat),
      backgroundColor: 'rgb(255, 99, 132)',
      borderColor: 'rgb(255, 99, 132)',
    },
    {
      label: 'Waist Size',
      data: state.characteristics.map(characteristic => characteristic.waistSize),
      backgroundColor: 'rgb(54, 162, 235)',
      borderColor: 'rgb(54, 162, 235)',
    },
    {
      label: 'Arm Size',
      data: state.characteristics.map(characteristic => characteristic.armSize),
      backgroundColor: 'rgb(255, 206, 86)',
      borderColor: 'rgb(255, 206, 86)',
    },
    {
      label: 'Thigh Size',
      data: state.characteristics.map(characteristic => characteristic.thighSize),
      backgroundColor: 'rgb(75, 192, 192)',
      borderColor: 'rgb(75, 192, 192)',
    },
    {
      label: 'Tricep Fat',
      data: state.characteristics.map(characteristic => characteristic.tricepFat),
      backgroundColor: 'rgb(153, 102, 255)',
      borderColor: 'rgb(153, 102, 255)',
    },
    {
      label: 'Abdomen Fat',
      data: state.characteristics.map(characteristic => characteristic.abdomenFat),
      backgroundColor: 'rgb(192, 147, 75)',
      borderColor: 'rgb(192, 147, 75)',
    },
    {
      label: 'Thigh Fat',
      data: state.characteristics.map(characteristic => characteristic.thighFat),
      backgroundColor: 'rgb(255, 99, 132)',
      borderColor: 'rgb(255, 99, 132)',
    },
  ]

  const isAddPopupOpen = state.popupOpen
  const isSelectedPopupOpen = state.selectedCharacteristicpopupOpen

  if (state.characteristics === undefined) {
    return <CircularProgress className={styles.waiting} />
  }

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
                  {state.characteristics.slice(-1).map((characteristic, index) => {
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
                  })}
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
                  labels={state.characteristics.map(characteristic =>
                    characteristic.date ? epochConverter(Number(characteristic.date), 'dd-mm-yyyy') : 'N/A'
                  )}
                  dataSetsData={characteristicsData}
                  onPointClick={handleShowSelectedCharacteristicsPopup}
                />
              }
              width="100%"
            />
          </>
        }
      />
      {isAddPopupOpen && <AddCharacteristicsPopup onClose={handlePopup} onSuccess={fetchCharacteristics} />}

      {isSelectedPopupOpen && (
        <ShowSelectedCharacteristicsPopup
          onClose={handleShowSelectedCharacteristicsPopup}
          onSuccess={fetchCharacteristics}
          data={state.selectedCharacteristic}
        />
      )}
    </>
  )
}
