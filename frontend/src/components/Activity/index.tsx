import * as React from 'react'
import { useNavigate, useParams } from 'react-router-dom'

import { Dropdown } from '../Dropdown'

import { epochConverter } from '../../../utils/epochConverter'

import { deleteGymActivity } from '../../../services/gymServices'
import { deleteWaterActivity } from '../../../services/waterServices'

import styles from './styles.module.css'

type ActivityProps = {
  activity: {
    id: number
    type: string
    date: number
  }
  onDeleteSuccess: () => void
}

export function Activity({ activity, onDeleteSuccess }: ActivityProps) {
  const navigate = useNavigate()
  const id = useParams().aid

  async function handleDeleteActivity() {
    if (confirm('Are you sure you want to delete this activity?')) {
      try{
        switch (activity.type) {
          case 'gym':
            await deleteGymActivity(activity.id.toString())
            break
          case 'water':
            await deleteWaterActivity(activity.id.toString())
            break
          default:
            throw new Error('Unknown activity type')
        }
        
        onDeleteSuccess()
      } catch (error) {
        console.error('Error deleting activity:', error)
      }
    }

  }


  function handleGymActivityClick(activityId: number) {
    navigate(`/athletes/${id}/gym/${activityId}`)
  }

  function handleWaterActivityClick(activityId: number) {
    navigate(`/athletes/${id}/water/${activityId}`)
  }

  return (
    <div className={styles.activity}>
      <Dropdown
        options={[
          { label: 'Delete', onClick: handleDeleteActivity }
        ]}
      />
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
