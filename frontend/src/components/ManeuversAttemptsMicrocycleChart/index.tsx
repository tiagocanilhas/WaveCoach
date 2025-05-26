import * as React from 'react'

import { Maneuver } from '../../types/Maneuver'

import { Bar } from 'react-chartjs-2'
import { Chart as ChartJS, BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title } from 'chart.js'

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title)

type ManeuversAttemptsMicrocycleChartProps = {
  labels: string[]
  successesData: number[]
  failuresData: number[]
  rightSide: boolean
}

export function ManeuversAttemptsMicrocycleChart({
  labels,
  successesData,
  failuresData,
  rightSide,
}: ManeuversAttemptsMicrocycleChartProps) {
  const chartData = {
    labels,
    datasets: [
      {
        label: 'Successes',
        data: successesData,
        backgroundColor: 'rgb(0, 131, 22)',
        borderRadius: 4,
      },
      {
        label: 'Failures',
        data: failuresData,
        backgroundColor: 'rgb(255, 0, 0)',
        borderRadius: 4,
      },
    ],
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

  return <Bar data={chartData} options={options} height={400} />
}
