import * as React from 'react'
import { useState } from 'react'

import { Button } from '../Button'

import { epochConverter } from '../../utils/epochConverter'

import styles from './styles.module.css'

type SelectedCycle = {
  mesocycleId: number
  microcycleId: number | null
} | null

type CyclesSelectProps = {
  cycles: any[]
  cycleSelected: SelectedCycle
  onSelect: (cycleSelected: SelectedCycle) => void
}

export function CyclesSelect({ cycles, cycleSelected, onSelect }: CyclesSelectProps) {
  const [isOpen, setIsOpen] = useState(true)

  function handleMesocycleClick(mesocycleId: number) {
    if (cycleSelected?.mesocycleId === mesocycleId && cycleSelected.microcycleId === null) onSelect(null)
    else onSelect({ mesocycleId, microcycleId: null })
  }

  function handleMicrocycleClick(mesocycleId: number, microcycleId: number) {
    if (cycleSelected?.mesocycleId === mesocycleId && cycleSelected.microcycleId === microcycleId) onSelect(null)
    else onSelect({ mesocycleId, microcycleId })
  }

  function handleToggleOpen() {
    setIsOpen(prev => !prev)
  }

  return (
    <div className={styles.container}>
      <Button onClick={handleToggleOpen} text={isOpen ? '▲' : '▼'} width="30px" height="30px" />
      {isOpen && (
        <div className={styles.cyclesContainer}>
          {(() => {
            let microcycleCounter = 1
            return cycles.map((mesocycle, mesocycleIndex) => (
              <div key={mesocycle.id} className={styles.mesocycleContainer}>
                <MesocycleButton
                  id={mesocycle.id}
                  number={mesocycleIndex + 1}
                  isSelected={cycleSelected?.mesocycleId === mesocycle.id && cycleSelected?.microcycleId === null}
                  onClick={handleMesocycleClick}
                />
                <div className={styles.microcycles}>
                  {mesocycle.microcycles.map(microcycle => (
                    <div key={microcycle.id} className={styles.microcycleContainer}>
                      <MicrocycleButton
                        mesocycleId={mesocycle.id}
                        id={microcycle.id}
                        number={microcycleCounter++}
                        isSelected={cycleSelected?.microcycleId === microcycle.id}
                        onClick={handleMicrocycleClick}
                      />
                      <div className={styles.microcycleTime}>
                        <p>{epochConverter(microcycle.startTime, 'dd/mm')}</p>
                        <p>{epochConverter(microcycle.endTime, 'dd/mm')}</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ))
          })()}
        </div>
      )}
    </div>
  )
}

type MesocycleButtonProps = {
  id: number
  number: number
  isSelected: boolean
  onClick: (mesocycleId: number) => void
}

function MesocycleButton({ id, number, isSelected, onClick }: MesocycleButtonProps) {
  return (
    <div className={`${styles.mesocycle} ${isSelected ? styles.selected : ''}`} onClick={() => onClick(id)}>
      {`Mesocycle ${number}`}
    </div>
  )
}

type MicrocycleButtonProps = {
  mesocycleId: number
  id: number
  number: number
  isSelected: boolean
  onClick: (mesocycleId: number, microcycleId: number) => void
}

function MicrocycleButton({ mesocycleId, id, number, isSelected, onClick }: MicrocycleButtonProps) {
  return (
    <div className={`${styles.microcycle} ${isSelected ? styles.selected : ''}`} onClick={() => onClick(mesocycleId, id)}>
      {`Microcycle ${number}`}
    </div>
  )
}
