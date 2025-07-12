import * as React from 'react'

import styles from './styles.module.css'

export function PlaceWithTrophy({ place }: { place: number }) {
  const emoji = place === 1 ? '🏆' : place === 2 ? '🥈' : place === 3 ? '🥉' : '🏄‍♂️'

  function toOrdinal(n: number): string {
    const s = ['th', 'st', 'nd', 'rd']
    const v = n % 100
    return n + (s[(v - 20) % 10] || s[v] || s[0])
  }

  return (
    <div className={styles.trophy}>
      <span>{emoji}</span>
      <span>{toOrdinal(place)}</span>
    </div>
  )
}
