import * as React from 'react'

import { Bar } from 'react-chartjs-2'
import { Chart as ChartJS, BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title } from 'chart.js'

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title)

type ManeuversAttemptsMesocycleChartProps = {
  labels: string[]
  microcycleNames: string[]
  attemptsByManeuver: Record<string, number[]>
  rightSide: boolean
}

export function ManeuversAttemptsMesocycleChart({
  labels,
  microcycleNames,
  attemptsByManeuver,
  rightSide,
}: ManeuversAttemptsMesocycleChartProps) {
  const microcyclescolors = [
    'rgb(0, 131, 22)', // Green
    'rgb(255, 0, 0)', // Red
    'rgb(0, 0, 255)', // Blue
    'rgb(255, 165, 0)', // Orange
    'rgb(128, 0, 128)', // Purple
    'rgb(255, 192, 203)', // Pink
    'rgb(128, 128, 0)', // Olive
    'rgb(0, 128, 128)', // Teal
    'rgb(192, 192, 192)', // Silver
    'rgb(105, 105, 105)', // DimGray
  ]

  const datasets = microcycleNames.map((name, index) => ({
    label: name,
    data: labels.map(label => attemptsByManeuver[label]?.[index] || 0),
    backgroundColor: microcyclescolors[index % microcyclescolors.length],
    borderRadius: 4,
  }))

  const data = {
    labels,
    datasets,
  }

  const options = {
    plugins: {
      title: {
        display: true,
        text: `Total Maneuvers (${rightSide ? 'Right' : 'Left'} Side)`,
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

  return <Bar data={data} options={options} height={400} />
}
