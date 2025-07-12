import * as React from 'react'

import { Dropdown } from '../Dropdown'
import { ScrollableText } from '../ScrollableText'
import { PlaceWithTrophy } from '../PlaceWithTrophy'

import { epochConverter } from '../../../../utils/epochConverter'

import { Competition } from '../../types/Competition'

import { deleteCompetition } from '../../../../services/athleteServices'

import styles from './styles.module.css'

type CompetitionCardProps = {
  competition: Competition
  onDeleteSuccess: () => void
  onClick: (competition: Competition) => void
}

export function CompetitionCard({ competition, onDeleteSuccess, onClick }: CompetitionCardProps) {
  async function handleDeleteCompetition() {
    if (confirm('Are you sure you want to delete this competition?')) {
      try {
        await deleteCompetition(competition.uid, competition.id)
        onDeleteSuccess()
      } catch (error) {
        console.error('Error deleting competition:', error)
      }
    }
  }

  return (
    <div className={styles.competitionCard}>
      <Dropdown options={[{ label: 'Delete', onClick: handleDeleteCompetition }]} />

      <PlaceWithTrophy place={competition.place} />

      <ScrollableText text={competition.name} className={styles.name} onClick={() => onClick(competition)} />

      <div className={styles.bottom}>
        <p className={styles.location}>{competition.location}</p>

        <p className={styles.date}>{epochConverter(competition.date, 'dd/mm/yyyy')}</p>
      </div>
    </div>
  )
}
