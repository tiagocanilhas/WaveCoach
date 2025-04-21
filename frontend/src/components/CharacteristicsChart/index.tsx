import * as React from 'react'
import { Line } from 'react-chartjs-2'
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js'
import { CharacteristicsData } from '../../pages/Characterisitcs/index'

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend)

type CharacteristicsChartProps = {
  labels: string[]
  dataSetsData: CharacteristicsData[]
  onPointClick: (index: number) => void
}

export function CharacteristicsChart({ labels, dataSetsData, onPointClick }: CharacteristicsChartProps) {
  const data = {
    labels: labels,
    datasets: dataSetsData.map(dataSet => ({
      label: dataSet.label,
      data: dataSet.data,
      backgroundColor: dataSet.backgroundColor,
      borderColor: dataSet.borderColor,
      hidden: true,
      spanGaps: true,
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
    onClick: (e: any, elements: any) => {
      if (elements.length > 0) {
        const index = elements[0].index
        //const datasetIndex = elements[0].datasetIndex;
        //const dataset = data.datasets[datasetIndex];
        //const label = dataset.label;
        onPointClick(index)
      }
    },
  }

  return (
    <>
      <Line options={options} data={data} />
    </>
  )
}
