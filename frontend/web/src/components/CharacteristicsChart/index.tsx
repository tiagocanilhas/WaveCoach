import * as React from 'react'
import { Line } from 'react-chartjs-2'
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js'
import { CharacteristicsData } from '../../types/CharacterisitcsData'
import { getRelativePosition } from 'chart.js/helpers'
import { useRef } from 'react'
import { Characteristics } from '../../types/Characteristics'

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend)

type CharacteristicsChartProps = {
  labels: string[]
  data: Characteristics[]
  onPointClick: (index: number) => void
}

export function CharacteristicsChart({ labels, data, onPointClick }: CharacteristicsChartProps) {
  const chartRef = useRef<any>(null)

  const chartData: CharacteristicsData[] = [
    {
      label: 'Height',
      data: data.map(characteristic => characteristic.height),
      backgroundColor: 'rgb(192, 147, 75)',
      borderColor: 'rgb(192, 147, 75)',
    },
    {
      label: 'Weight',
      data: data.map(characteristic => characteristic.weight),
      backgroundColor: 'rgb(153, 102, 255)',
      borderColor: 'rgb(153, 102, 255)',
    },
    {
      label: 'Calories',
      data: data.map(characteristic => characteristic.calories),
      backgroundColor: 'rgb(75, 192, 192)',
      borderColor: 'rgb(75, 192, 192)',
    },
    {
      label: 'Body Fat',
      data: data.map(characteristic => characteristic.bodyFat),
      backgroundColor: 'rgb(255, 99, 132)',
      borderColor: 'rgb(255, 99, 132)',
    },
    {
      label: 'Waist Size',
      data: data.map(characteristic => characteristic.waistSize),
      backgroundColor: 'rgb(54, 162, 235)',
      borderColor: 'rgb(54, 162, 235)',
    },
    {
      label: 'Arm Size',
      data: data.map(characteristic => characteristic.armSize),
      backgroundColor: 'rgb(255, 206, 86)',
      borderColor: 'rgb(255, 206, 86)',
    },
    {
      label: 'Thigh Size',
      data: data.map(characteristic => characteristic.thighSize),
      backgroundColor: 'rgb(75, 192, 192)',
      borderColor: 'rgb(75, 192, 192)',
    },
    {
      label: 'Tricep Fat',
      data: data.map(characteristic => characteristic.tricepFat),
      backgroundColor: 'rgb(153, 102, 255)',
      borderColor: 'rgb(153, 102, 255)',
    },
    {
      label: 'Abdomen Fat',
      data: data.map(characteristic => characteristic.abdomenFat),
      backgroundColor: 'rgb(192, 147, 75)',
      borderColor: 'rgb(192, 147, 75)',
    },
    {
      label: 'Thigh Fat',
      data: data.map(characteristic => characteristic.thighFat),
      backgroundColor: 'rgb(255, 99, 132)',
      borderColor: 'rgb(255, 99, 132)',
    },
  ]

  const dataToBuildChart = {
    labels: labels,
    datasets: chartData.map(dataSet => ({
      label: dataSet.label,
      data: dataSet.data,
      backgroundColor: dataSet.backgroundColor,
      borderColor: dataSet.borderColor,
      hidden: true,
      spanGaps: true,
      borderWidth: 5,
    })),
  }

  const options = {
    responsive: true,
    plugins: {
      legend: {
        position: 'bottom' as const,
      },
      title: {
        display: true,
        text: 'Characteristics',
        font: {
          size: 20,
        },
      },
    },
    onClick: (e: any) => {
      const chart = chartRef.current
      if (!chart) return

      const position = getRelativePosition(e, chart)
      const xAxis = chart.scales['x']
      const labelIndex = xAxis.getValueForPixel(position.x)

      if (labelIndex !== undefined && labelIndex >= 0 && labelIndex < labels.length) {
        onPointClick(labelIndex)
      }
    },
  }

  return <Line ref={chartRef} options={options} data={dataToBuildChart} />
}
