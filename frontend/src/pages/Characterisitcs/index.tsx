import * as React from 'react'
import { useEffect } from 'react'
import { useParams } from 'react-router-dom'

import { CircularProgress, TextField } from '@mui/material'
import { CharacteristicsChart } from '../../components/CharacteristicsChart'
import { AddCharacteristicsPopup } from '../../components/AddCharacteristicsPopup'
import { ShowSelectedCharacteristicsPopup } from '../../components/ShowSelectedCharacteristicsPopup'
import { Button } from '../../components/Button'
import { Card } from '../../components/Card'

import { getCharacteristics } from '../../services/athleteServices'

import { handleError } from '../../utils/handleError'

import styles from './styles.module.css'
import { epochConverter } from '../../utils/epochConverter'

export type Characteristics = {
  date?: string
  height?: number
  weight?: number
  calories?: number
  bodyFat?: number
  waistSize?: number
  armSize?: number
  thighSize?: number
  tricepFat?: number
  abdomenFat?: number
  thighFat?: number
}

export type CharacteristicsData = {
  label: string
  data: number[]
  backgroundColor: string
  borderColor: string
}

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
  const [state, dispatch] = React.useReducer(reducer, initialState)
  const id = useParams().aid as string

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
      borderColor: 'rgba(192, 104, 75, 0.2)',
    },
    {
      label: 'Weight',
      data: state.characteristics.map(characteristic => characteristic.weight),
      backgroundColor: 'rgb(153, 102, 255)',
      borderColor: 'rgba(153, 102, 255, 0.2)',
    },
    {
      label: 'Calories',
      data: state.characteristics.map(characteristic => characteristic.calories),
      backgroundColor: 'rgb(75, 192, 192)',
      borderColor: 'rgba(75, 192, 192, 0.2)',
    },
    {
      label: 'Body Fat',
      data: state.characteristics.map(characteristic => characteristic.bodyFat),
      backgroundColor: 'rgb(255, 99, 132)',
      borderColor: 'rgba(255, 99, 132, 0.2)',
    },
    {
      label: 'Waist Size',
      data: state.characteristics.map(characteristic => characteristic.waistSize),
      backgroundColor: 'rgb(54, 162, 235)',
      borderColor: 'rgba(54, 162, 235, 0.2)',
    },
    {
      label: 'Arm Size',
      data: state.characteristics.map(characteristic => characteristic.armSize),
      backgroundColor: 'rgb(255, 206, 86)',
      borderColor: 'rgba(255, 206, 86, 0.2)',
    },
    {
      label: 'Thigh Size',
      data: state.characteristics.map(characteristic => characteristic.thighSize),
      backgroundColor: 'rgb(75, 192, 192)',
      borderColor: 'rgba(75, 192, 192, 0.2)',
    },
    {
      label: 'Tricep Fat',
      data: state.characteristics.map(characteristic => characteristic.tricepFat),
      backgroundColor: 'rgb(153, 102, 255)',
      borderColor: 'rgba(153, 102, 255, 0.2)',
    },
    {
      label: 'Abdomen Fat',
      data: state.characteristics.map(characteristic => characteristic.abdomenFat),
      backgroundColor: 'rgb(192, 147, 75)',
      borderColor: 'rgba(192, 104, 75, 0.2)',
    },
    {
      label: 'Thigh Fat',
      data: state.characteristics.map(characteristic => characteristic.thighFat),
      backgroundColor: 'rgb(255, 99, 132)',
      borderColor: 'rgba(255, 99, 132, 0.2)',
    },
  ]

  const isAddPopupOpen = state.popupOpen
  const isSelectedPopupOpen = state.selectedCharacteristicpopupOpen

  if (state.characteristics === undefined) {
    return <CircularProgress className={styles.waiting} />
  }

  return (
    <>
      <div className={styles.container}>
        <div className={styles.leftColumn}>
          <Button text="Add Characteristics" onClick={handlePopup} width="100%" height="50px" />
          <Card
            content={
              <div className={styles.characteristics}>
                <h2 className={styles.header}>Last Characteristics</h2>
                {state.characteristics.slice(-1).map((characteristic, index) => (
                  <div key={index} className={styles.characteristic}>
                    <span>Date: {characteristic.date ? epochConverter(Number(characteristic.date), 'dd-mm-yyyy') : 'N/A'}</span>
                    <span>Height: {characteristic.height ? characteristic.height : 'N/A'}</span>
                    <span>Weight: {characteristic.weight ? characteristic.weight : 'N/A'}</span>
                    <span>Calories: {characteristic.calories ? characteristic.calories : 'N/A'}</span>
                    <span>Body fat: {characteristic.bodyFat ? characteristic.bodyFat : 'N/A'}</span>
                    <span>Waist size: {characteristic.waistSize ? characteristic.waistSize : 'N/A'}</span>
                    <span>Arm size: {characteristic.armSize ? characteristic.armSize : 'N/A'}</span>
                    <span>Thigh size: {characteristic.thighSize ? characteristic.thighSize : 'N/A'}</span>
                    <span>Tricep fat: {characteristic.tricepFat ? characteristic.tricepFat : 'N/A'}</span>
                    <span>Abdomen fat: {characteristic.abdomenFat ? characteristic.abdomenFat : 'N/A'}</span>
                    <span>Thigh fat: {characteristic.thighFat ? characteristic.thighFat : 'N/A'}</span>
                  </div>
                ))}
              </div>
            }
          />
        </div>
        <div className={styles.chart}>
          <CharacteristicsChart
            labels={state.characteristics.map(characteristic =>
              characteristic.date ? epochConverter(Number(characteristic.date), 'dd-mm-yyyy') : 'N/A'
            )}
            dataSetsData={characteristicsData}
            onPointClick={handleShowSelectedCharacteristicsPopup}
          />
        </div>
      </div>

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
