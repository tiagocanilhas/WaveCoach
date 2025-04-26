import * as React from 'react'
import { Line } from 'react-chartjs-2'
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js'
import { CharacteristicsData } from '../../pages/Characterisitcs/index'
import { getRelativePosition } from 'chart.js/helpers'

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend)

type CharacteristicsChartProps = {
  labels: string[]
  dataSetsData: CharacteristicsData[]
  onPointClick: (index: number) => void
}

export function CharacteristicsChart({ labels, dataSetsData, onPointClick }: CharacteristicsChartProps) {
  const chartRef = React.useRef<any>(null);
  
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
    onClick: (e: any) => {
      const chart = chartRef.current;
      if (!chart) return;

      // Obtém a posição do clique relativo ao gráfico
      const position = getRelativePosition(e, chart);

      // Verifica se o clique ocorreu próximo a uma label no eixo X
      const xAxis = chart.scales['x'];
      const labelIndex = xAxis.getValueForPixel(position.x);

      if (labelIndex !== undefined && labelIndex >= 0 && labelIndex < labels.length) {
        console.log(`Label clicked: ${labels[labelIndex]}`);
        onPointClick(labelIndex);
      }
    },
  }

  return (
    <>
      <Line ref={chartRef} options={options} data={data} />
    </>
  )
}
