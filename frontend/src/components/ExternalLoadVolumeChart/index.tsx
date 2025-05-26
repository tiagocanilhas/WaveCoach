import * as React from 'react'
import { Bar } from 'react-chartjs-2'
import { Chart as ChartJS, BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title } from 'chart.js'

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title)

type ExternalLoadVolumeChartProps = {
  labels: string[]
  waterTime: number[]
  waveCount: number[]
  maneuverAttempts: number[]
}

export function ExternalLoadVolumeChart({ labels, waterTime, waveCount, maneuverAttempts }: ExternalLoadVolumeChartProps) {
  const chartData = {
    labels,
    datasets: [
      {
        label: 'Water Time (min)',
        data: waterTime,
        backgroundColor: 'rgb(0, 153, 255)',
        borderRadius: 4,
      },
      {
        label: 'Wave Count',
        data: waveCount,
        backgroundColor: 'rgb(255, 196, 0)',
        borderRadius: 4,
      },
      {
        label: 'Maneuver Attempts',
        data: maneuverAttempts,
        backgroundColor: 'rgb(0, 131, 22)',
        borderRadius: 4,
      },
    ],
  }

  const options = {
    plugins: {
      title: {
        display: true,
        text: 'External Load (Volume)',
        font: { size: 18 },
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
