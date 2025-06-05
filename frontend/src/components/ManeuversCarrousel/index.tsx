import * as React from 'react'
import { Pie, Bar } from 'react-chartjs-2'
import { Chart as ChartJS, ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title } from 'chart.js'
ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title)

import { motion, AnimatePresence } from 'framer-motion'

import { Button } from '../Button'

import { Maneuver } from '../../types/Maneuver'

import styles from './styles.module.css'
import { useState } from 'react'

type ManeuverSummary = {
  id: number
  name: string
  attempts: number
  accuracy: number
}

type ManeuversCarrouselProps = {
  maneuvers: Maneuver[]
}

export function ManeuversCarrousel({ maneuvers }: ManeuversCarrouselProps) {
  const views: ('pie' | 'bar')[] = ['pie', 'bar']
  const [viewIndex, setViewIndex] = useState(0)
  const [direction, setDirection] = useState<-1 | 1>(1)

  function summarizeManeuvers(maneuvers: Maneuver[]): ManeuverSummary[] {
    const summaryMap = new Map<number, { name: string; attempts: number; successes: number }>()

    for (const maneuver of maneuvers) {
      const entry = summaryMap.get(maneuver.waterManeuverId)
      if (entry) {
        entry.attempts += 1
        if (maneuver.success) entry.successes += 1
      } else {
        summaryMap.set(maneuver.waterManeuverId, {
          name: maneuver.name,
          attempts: 1,
          successes: maneuver.success ? 1 : 0,
        })
      }
    }

    return Array.from(summaryMap.entries()).map(([id, { name, attempts, successes }]) => ({
      id,
      name,
      attempts,
      accuracy: parseFloat(((successes / attempts) * 100).toFixed(2)),
    }))
  }

  const summaries = summarizeManeuvers(maneuvers)

  const colors = ['#4dc9f6', '#f67019', '#f53794', '#537bc4', '#acc236', '#166a8f', '#00a950', '#58595b', '#8549ba']

  const pieData = {
    labels: summaries.map(s => s.name),
    datasets: [
      {
        label: 'Attempts',
        data: summaries.map(s => s.attempts),
        backgroundColor: colors.slice(0, summaries.length),
        borderColor: '#ffffff',
        borderWidth: 2,
      },
    ],
  }

  const barData = {
    labels: summaries.map(s => s.name),
    datasets: [
      {
        label: 'Accuracy (%)',
        data: summaries.map(s => s.accuracy),
        backgroundColor: '#4dc9f6',
        borderColor: '#166a8f',
        borderWidth: 1,
      },
    ],
  }

  const pieOptions = {
    plugins: {
      title: {
        display: true,
        text: 'Number of Attempts per Maneuver',
        font: {
          size: 18,
        },
      },
      legend: {
        position: 'top' as const,
      },
    },
    responsive: true,
    maintainAspectRatio: false,
  }

  const barOptions = {
    plugins: {
      title: {
        display: true,
        text: 'Accuracy (%) per Maneuver',
        font: {
          size: 18,
        },
      },
      legend: {
        display: false,
      },
    },
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        beginAtZero: true,
        max: 100,
      },
    },
  }

  function prevView() {
    setDirection(-1)
    setViewIndex(v => (v === 0 ? views.length - 1 : v - 1))
  }

  function nextView() {
    setDirection(1)
    setViewIndex(v => (v === views.length - 1 ? 0 : v + 1))
  }

  return (
    <div className={styles.container}>
      <div className={styles.chartWrapper}>
        <AnimatePresence mode="wait">
          <motion.div
            key={views[viewIndex]}
            initial={{ opacity: 1, x: direction === 1 ? '100%' : '-100%' }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 1, x: direction === 1 ? '-100%' : '100%' }}
            transition={{ duration: 0.3 }}
          >
            {views[viewIndex] === 'pie' ? (
              <Pie data={pieData} options={pieOptions} height={400} />
            ) : (
              <Bar data={barData} options={barOptions} height={400} />
            )}
          </motion.div>
        </AnimatePresence>
      </div>

      <div className={styles.buttons}>
        <Button text="Previous" onClick={prevView} width="130px" height="40px" />
        <Button text="Next" onClick={nextView} width="130px" height="40px" />
      </div>
    </div>
  )
}
