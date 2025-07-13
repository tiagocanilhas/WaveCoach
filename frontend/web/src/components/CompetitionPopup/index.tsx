import * as React from 'react'
import { useState } from 'react'

import { ScrollableText } from '../ScrollableText'
import { Popup } from '../Popup'
import { Divisor } from '../Divisor'
import { PlaceWithTrophy } from '../PlaceWithTrophy'
import { Button } from '../Button'
import { EditCompetitionPopup } from '../EditCompetitionPopup'

import { epochConverter } from '../../../../utils/epochConverter'

import { Competition } from '../../types/Competition'

import { useAuthentication } from '../../hooks/useAuthentication'

import styles from './styles.module.css'

type CompetitionPopupProps = {
  competition: Competition
  onClose: () => void
  onUpdateSuccess: () => void
}

export function CompetitionPopup({ competition, onClose, onUpdateSuccess }: CompetitionPopupProps) {
  const [editing, setEditing] = useState(false)
  const [user] = useAuthentication()

  function handleToggleEdit() {
    setEditing(p => !p)
  }

  return (
    <>
      <Popup
        title="Competition Details"
        content={
          <Divisor
            left={
              <div className={styles.details}>
                {user.isCoach && <Button text="Edit" onClick={handleToggleEdit} /> }
                <p className={styles.date}>{epochConverter(competition.date, 'dd/mm/yyyy')}</p>
                <p className={styles.name}>{competition.name}</p>
                <PlaceWithTrophy place={competition.place} />
                <p className={styles.location}>{competition.location}</p>
              </div>
            }
            right={
              <div className={styles.heatsContainer}>
                {competition.heats.length === 0 ? (
                  <p className={styles.noHeats}>No heats available for this competition</p>
                ) : (
                  competition.heats.map((heat, index) => (
                    <div key={heat.id} className={styles.heat}>
                      <h2>Heat {index + 1}</h2>
                      <h3>{heat.score} Points</h3>
                      <p>{heat.waterActivity.condition}</p>
                      <div className={styles.wavesContainer}>
                        {heat.waterActivity.waves.length === 0 ? (
                          <p>No waves for this heat</p>
                        ) : (
                          heat.waterActivity.waves.map((wave, index) => (
                            <div key={wave.id} className={styles.waveDetails}>
                              <h2>
                                Wave {index + 1} {wave.rightSide ? '➡️' : '⬅️'}
                              </h2>
                              <p>{wave.points} Points</p>
                              <div className={styles.maneuversContainer}>
                                {wave.maneuvers.map(maneuver => (
                                  <div key={maneuver.id} className={styles.maneuver}>
                                    <img src={maneuver.url || `/images/no_image.svg`} alt="Maneuver" />
                                    <ScrollableText text={`${maneuver.success ? '✅' : '❌'} - ${maneuver.name}`} />
                                  </div>
                                ))}
                              </div>
                            </div>
                          ))
                        )}
                      </div>
                    </div>
                  ))
                )}
              </div>
            }
          />
        }
        onClose={onClose}
      />

      {editing && <EditCompetitionPopup competition={competition} onClose={handleToggleEdit} onSuccess={onUpdateSuccess} />}
    </>
  )
}
