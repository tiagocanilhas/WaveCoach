import * as React from 'react'
import { Bar } from 'react-chartjs-2'
import { Chart as ChartJS, BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title } from 'chart.js'

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title)

type InternalLoadChartProps = {
  labels: string[]
  sessionPseData: number[]
  trimpData: number[]
}

export function InternalLoadChart({ labels, sessionPseData, trimpData }: InternalLoadChartProps) {
  const chartData = {
    labels,
    datasets: [
      {
        label: 'Session PSE',
        data: sessionPseData,
        backgroundColor: 'rgb(0, 153, 255)',
        borderRadius: 4,
      },
      {
        label: 'TRIMP',
        data: trimpData,
        backgroundColor: 'rgb(255, 196, 0)',
        borderRadius: 4,
      },
    ],
  }

  const options = {
    plugins: {
      title: {
        display: true,
        text: 'Internal Load',
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
