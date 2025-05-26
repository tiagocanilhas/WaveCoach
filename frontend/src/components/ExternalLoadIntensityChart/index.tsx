import * as React from 'react'
import { Bar } from 'react-chartjs-2'
import { Chart as ChartJS, BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title } from 'chart.js'

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title)

type ExternalLoadIntensityChartProps = {
  labels: string[]
  wavesPerMinute: number[]
  maneuversPerWave: number[]
}

export function ExternalLoadIntensityChart({ labels, wavesPerMinute, maneuversPerWave }: ExternalLoadIntensityChartProps) {
  const chartData = {
    labels,
    datasets: [
      {
        label: 'Waves per Minute',
        data: wavesPerMinute,
        backgroundColor: 'rgb(0, 153, 255)',
        borderRadius: 4,
      },
      {
        label: 'Maneuvers per Wave',
        data: maneuversPerWave,
        backgroundColor: 'rgb(255, 196, 0)',
        borderRadius: 4,
      },
    ],
  }
  const options = {
    plugins: {
      title: {
        display: true,
        text: 'External Load (Intensity)',
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
