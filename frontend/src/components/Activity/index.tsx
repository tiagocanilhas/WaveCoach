import * as React from 'react'
import { useNavigate, useParams } from 'react-router-dom'

import { epochConverter } from '../../utils/epochConverter'

import styles from './styles.module.css'

type ActivityProps = {
  activity: {
    id: number
    type: string
    date: number
  }
}

export function Activity({ activity }: ActivityProps) {
  const navigate = useNavigate()
  const id = useParams().aid

  function handleGymActivityClick(activityId: number) {
    navigate(`/athletes/${id}/gym/${activityId}`)
  }

  function handleWaterActivityClick(activityId: number) {
    navigate(`/athletes/${id}/water/${activityId}`)
  }

  return (
    <div className={styles.activity}>
      <div className={styles.imageContainer}>
        <img
          src={`/images/no_image.svg`}
          alt={activity.type || 'No Image'}
          style={{ cursor: activity.type && activity.type !== 'null' ? 'pointer' : 'default' }}
          onClick={
            activity.type && activity.type !== 'null'
              ? activity.type === 'gym'
                ? () => handleGymActivityClick(activity.id)
                : () => handleWaterActivityClick(activity.id)
              : undefined
          }
        />
        <span>
          {activity.type && activity.type !== 'null' ? activity.type.charAt(0).toUpperCase() + activity.type.slice(1) : ''}
        </span>
      </div>
      <h3>{epochConverter(activity.date, 'dd-mm-yyyy')}</h3>
    </div>
  )
}
