import * as React from 'react'
import { Bar } from 'react-chartjs-2'
import { Chart as ChartJS, BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title } from 'chart.js'

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title)

type ManeuversAccuracyMicrocycleChartProps = {
  labels: string[]
  accuracyData: number[]
  rightSide: boolean
}

export function ManeuversAccuracyMicrocycleChart({ labels, accuracyData, rightSide }: ManeuversAccuracyMicrocycleChartProps) {
  const chartData = {
    labels,
    datasets: [
      {
        label: 'Success Rate (%)',
        data: accuracyData,
        backgroundColor: 'rgb(0, 153, 255)',
        borderRadius: 4,
      },
    ],
  }

  const options = {
    plugins: {
      title: {
        display: true,
        text: `Success Rate per Maneuver (${rightSide ? 'Right' : 'Left'} Side)`,
        font: {
          size: 18,
        },
      },
      legend: {
        position: 'top' as const,
      },
      tooltip: {
        callbacks: {
          label: (context: any) => `${context.raw}%`,
        },
      },
    },
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        beginAtZero: true,
        max: 100,
        title: {
          display: true,
          text: 'Success Rate (%)',
        },
      },
    },
  }

  return <Bar data={chartData} options={options} height={400} />
}
