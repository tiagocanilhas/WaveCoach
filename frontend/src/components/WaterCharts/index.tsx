import * as React from 'react'

import { InternalLoadChart } from '../InternalLoadChart'
import { ExternalLoadVolumeChart } from '../ExternalLoadVolumeChart'
import { ExternalLoadIntensityChart } from '../ExternalLoadIntensityChart'
import { ManeuversAttemptsMicrocycleChart } from '../ManeuversAttemptsMicrocycleChart'
import { ManeuversAccuracyMicrocycleChart } from '../ManeuversAccuracyMicrocycleChart'
import { ManeuversAttemptsMesocycleChart } from '../ManeuversAttemptsMesocycleChart'
import { ManeuversAccuracyMesocycleChart } from '../ManeuversAccuracyMesocycleChart'
import { Card } from '../Card'

import { Maneuver } from '../../types/Maneuver'
import { WaterCalendar } from '../../types/WaterCalendar'

import { epochConverter } from '../../../utils/epochConverter'

import styles from './styles.module.css'

type WaterChartsProps = {
  data: WaterCalendar
  selected: { mesocycleId: number; microcycleId?: number } | null
}

export function WaterCharts({ data, selected }: WaterChartsProps) {
  if (!data || !selected || !selected.mesocycleId) return null

  function getInternalLoadData(workouts: any[]): { sessionPseData: number[]; trimpData: number[] } {
    const sessionPseData = workouts.map(w => (w.pse * w.duration) / 60)
    const trimpData = workouts.map(w => w.heartRate)
    return { sessionPseData, trimpData }
  }

  function getExternalLoadVolumeData(workouts: any[]): { waterTime: number[]; waveCount: number[]; maneuverAttempts: number[] } {
    const waterTime = workouts.map(w => w.duration / 60)
    const waveCount = workouts.map(w => w.waves.length)
    const maneuverAttempts = workouts.map(w => w.waves.reduce((acc, wave) => acc + wave.maneuvers.length, 0))
    return { waterTime, waveCount, maneuverAttempts }
  }

  function getExternalLoadIntensityData(workouts: any[]): { wavesPerMinute: number[]; maneuversPerWave: number[] } {
    const wavesPerMinute = workouts.map(w => w.waves.length / (w.duration / 60))
    const maneuversPerWave = workouts.map(w => w.waves.reduce((acc, wave) => acc + wave.maneuvers.length, 0) / w.waves.length)
    return { wavesPerMinute, maneuversPerWave }
  }

  if (selected.mesocycleId && !selected.microcycleId) {
    const mesocycle = data.mesocycles.find(m => m.id === selected.mesocycleId)
    const workouts = mesocycle?.microcycles.flatMap(mc => mc.activities) ?? []

    const dateLabels = workouts.map(w => epochConverter(w.date, 'dd-mm-yyyy'))

    const { sessionPseData, trimpData } = getInternalLoadData(workouts)
    const { waterTime, waveCount, maneuverAttempts } = getExternalLoadVolumeData(workouts)
    const { wavesPerMinute, maneuversPerWave } = getExternalLoadIntensityData(workouts)

    const waves = workouts.flatMap(w => w.waves)

    // Maneuvers Attempts Data (Right Side)
    const maneuverR = waves.filter(w => w.rightSide).flatMap(wave => wave.maneuvers)
    const maneuversRLabels = maneuverR.map(m => m.name).filter((value, index, self) => self.indexOf(value) === index)

    const rightManeuvers = mesocycle.microcycles.map(microcycle => {
      const microcycleManeuvers = maneuverR.filter(m =>
        microcycle.activities.some(w => w.waves.some(wave => wave.maneuvers.some(man => man.id === m.id)))
      )
      return { microcycleId: microcycle.id, maneuvers: countManeuvers(microcycleManeuvers) }
    })
    const attemptsByManeuverR = rightManeuvers.reduce(
      (acc, curr) => {
        Object.entries(curr.maneuvers).forEach(([_, maneuver]) => {
          if (!acc[maneuver.name]) acc[maneuver.name] = []
          acc[maneuver.name].push(maneuver.success + maneuver.failure)
        })
        return acc
      },
      {} as Record<string, number[]>
    )
    const accuracyRightManeuvers = mesocycle.microcycles.map(microcycle => {
      const microcycleManeuvers = maneuverR.filter(m =>
        microcycle.activities.some(w => w.waves.some(wave => wave.maneuvers.some(man => man.id === m.id)))
      )
      return { microcycleId: microcycle.id, maneuvers: countManeuvers(microcycleManeuvers) }
    })

    const accuracyByManeuverR = accuracyRightManeuvers.reduce(
      (acc, curr) => {
        Object.entries(curr.maneuvers).forEach(([_, maneuver]) => {
          const total = maneuver.success + maneuver.failure
          const accuracy = total > 0 ? (maneuver.success / total) * 100 : 0
          if (!acc[maneuver.name]) acc[maneuver.name] = []
          acc[maneuver.name].push(accuracy)
        })
        return acc
      },
      {} as Record<string, number[]>
    )

    // Maneuvers Attempts Data (Left Side)
    const maneuverL = waves.filter(w => !w.rightSide).flatMap(wave => wave.maneuvers)
    const maneuversLLabels = maneuverL.map(m => m.name).filter((value, index, self) => self.indexOf(value) === index)

    const leftManeuvers = mesocycle.microcycles.map(microcycle => {
      const microcycleManeuvers = maneuverL.filter(m =>
        microcycle.activities.some(w => w.waves.some(wave => wave.maneuvers.some(man => man.id === m.id)))
      )
      return { microcycleId: microcycle.id, maneuvers: countManeuvers(microcycleManeuvers) }
    })
    const attemptsByManeuverL = leftManeuvers.reduce(
      (acc, curr) => {
        Object.entries(curr.maneuvers).forEach(([_, maneuver]) => {
          if (!acc[maneuver.name]) acc[maneuver.name] = []
          acc[maneuver.name].push(maneuver.success + maneuver.failure)
        })
        return acc
      },
      {} as Record<string, number[]>
    )
    const accuracyLeftManeuvers = mesocycle.microcycles.map(microcycle => {
      const microcycleManeuvers = maneuverL.filter(m =>
        microcycle.activities.some(w => w.waves.some(wave => wave.maneuvers.some(m => m.id === m.id)))
      )
      return { microcycleId: microcycle.id, maneuvers: countManeuvers(microcycleManeuvers) }
    })
    const accuracyByManeuverL = accuracyLeftManeuvers.reduce(
      (acc, curr) => {
        Object.entries(curr.maneuvers).forEach(([_, maneuver]) => {
          const total = maneuver.success + maneuver.failure
          const accuracy = total > 0 ? (maneuver.success / total) * 100 : 0
          if (!acc[maneuver.name]) acc[maneuver.name] = []
          acc[maneuver.name].push(accuracy)
        })
        return acc
      },
      {} as Record<string, number[]>
    )

    const microcycleNames = mesocycle.microcycles.map(mc => `Microcycle ${mc.id}`)

    return (
      <div className={styles.container}>
        <div className={styles.load}>
          <Card
            content={<InternalLoadChart labels={dateLabels} sessionPseData={sessionPseData} trimpData={trimpData} />}
            width="100%"
          />
          <Card
            content={
              <ExternalLoadVolumeChart
                labels={dateLabels}
                waterTime={waterTime}
                waveCount={waveCount}
                maneuverAttempts={maneuverAttempts}
              />
            }
            width="100%"
          />
          <Card
            content={
              <ExternalLoadIntensityChart
                labels={dateLabels}
                wavesPerMinute={wavesPerMinute}
                maneuversPerWave={maneuversPerWave}
              />
            }
            width="100%"
          />
        </div>
        <div className={styles.maneuvers}>
          <div className={styles.leftManeuvers}>
            <Card
              content={
                <ManeuversAttemptsMesocycleChart
                  labels={maneuversLLabels}
                  microcycleNames={microcycleNames}
                  attemptsByManeuver={attemptsByManeuverL}
                  rightSide={false}
                />
              }
              width="100%"
            />
            <Card
              content={
                <ManeuversAccuracyMesocycleChart
                  labels={maneuversLLabels}
                  microcycleNames={microcycleNames}
                  accuracyByManeuver={accuracyByManeuverL}
                  rightSide={false}
                />
              }
              width="100%"
            />
          </div>
          <div className={styles.rightManeuvers}>
            <Card
              content={
                <ManeuversAttemptsMesocycleChart
                  labels={maneuversRLabels}
                  microcycleNames={microcycleNames}
                  attemptsByManeuver={attemptsByManeuverR}
                  rightSide
                />
              }
              width="100%"
            />
            <Card
              content={
                <ManeuversAccuracyMesocycleChart
                  labels={maneuversRLabels}
                  microcycleNames={microcycleNames}
                  accuracyByManeuver={accuracyByManeuverR}
                  rightSide
                />
              }
              width="100%"
            />
          </div>
        </div>
      </div>
    )
  } else {
    const microcycle = data.mesocycles
      .find(m => m.id === selected.mesocycleId)
      .microcycles.find(mc => mc.id === selected.microcycleId)
    const workouts = microcycle.activities

    const dateLabels = workouts.map(w => epochConverter(w.date, 'dd-mm-yyyy'))

    const { sessionPseData, trimpData } = getInternalLoadData(workouts)
    const { waterTime, waveCount, maneuverAttempts } = getExternalLoadVolumeData(workouts)
    const { wavesPerMinute, maneuversPerWave } = getExternalLoadIntensityData(workouts)

    const waves = workouts.flatMap(w => w.waves)

    // Maneuvers Attempts Data (Right Side)
    const maneuverR = waves.filter(w => w.rightSide).flatMap(wave => wave.maneuvers)
    const maneuversRLabels = maneuverR.map(m => m.name).filter((value, index, self) => self.indexOf(value) === index)

    const rightManeuvers = countManeuvers(maneuverR)
    const rightSuccessesData = Object.keys(rightManeuvers).map(label => rightManeuvers[label].success || 0)
    const rightFailuresData = Object.keys(rightManeuvers).map(label => rightManeuvers[label].failure || 0)
    const rightAccuracyData = Object.keys(rightManeuvers).map(label => {
      const total = rightManeuvers[label].success + rightManeuvers[label].failure
      return total > 0 ? (rightManeuvers[label].success / total) * 100 : 0
    })

    // Maneuvers Attempts Data (Left Side)
    const maneuverL = waves.filter(w => !w.rightSide).flatMap(wave => wave.maneuvers)
    const maneuversLLabels = maneuverL.map(m => m.name).filter((value, index, self) => self.indexOf(value) === index)
    const leftManeuvers = countManeuvers(maneuverL)
    const leftSuccessesData = Object.keys(leftManeuvers).map(label => leftManeuvers[label].success || 0)
    const leftFailuresData = Object.keys(leftManeuvers).map(label => leftManeuvers[label].failure || 0)
    const maneuversLPercentages = Object.keys(leftManeuvers).map(label => {
      const total = leftManeuvers[label].success + leftManeuvers[label].failure
      return total > 0 ? (leftManeuvers[label].success / total) * 100 : 0
    })

    return (
      <div className={styles.container}>
        <div className={styles.load}>
          <Card
            content={<InternalLoadChart labels={dateLabels} sessionPseData={sessionPseData} trimpData={trimpData} />}
            width="100%"
          />
          <Card
            content={
              <ExternalLoadVolumeChart
                labels={dateLabels}
                waterTime={waterTime}
                waveCount={waveCount}
                maneuverAttempts={maneuverAttempts}
              />
            }
            width="100%"
          />
          <Card
            content={
              <ExternalLoadIntensityChart
                labels={dateLabels}
                wavesPerMinute={wavesPerMinute}
                maneuversPerWave={maneuversPerWave}
              />
            }
            width="100%"
          />
        </div>
        <div className={styles.maneuvers}>
          <div className={styles.leftManeuvers}>
            <Card
              content={
                <ManeuversAttemptsMicrocycleChart
                  labels={maneuversLLabels}
                  successesData={leftSuccessesData}
                  failuresData={leftFailuresData}
                  rightSide={false}
                />
              }
              width="100%"
            />
            <Card
              content={
                <ManeuversAccuracyMicrocycleChart
                  labels={maneuversLLabels}
                  accuracyData={maneuversLPercentages}
                  rightSide={false}
                />
              }
              width="100%"
            />
          </div>
          <div className={styles.rightManeuvers}>
            <Card
              content={
                <ManeuversAttemptsMicrocycleChart
                  labels={maneuversRLabels}
                  successesData={rightSuccessesData}
                  failuresData={rightFailuresData}
                  rightSide
                />
              }
              width="100%"
            />
            <Card
              content={<ManeuversAccuracyMicrocycleChart labels={maneuversRLabels} accuracyData={rightAccuracyData} rightSide />}
              width="100%"
            />
          </div>
        </div>
      </div>
    )
  }
}

type ManeuverCount = {
  name: string
  success: number
  failure: number
}

function countManeuvers(data: Maneuver[]): Record<string, ManeuverCount> {
  return data.reduce(
    (acc, curr) => {
      const id = curr.waterManeuverId

      if (!acc[id]) acc[id] = { name: curr.name, success: 0, failure: 0 }

      if (curr.success) acc[id].success += 1
      else acc[id].failure += 1

      acc[id].name = curr.name

      return acc
    },
    {} as Record<string, ManeuverCount>
  )
}
