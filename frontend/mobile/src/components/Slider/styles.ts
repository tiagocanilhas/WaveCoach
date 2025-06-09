import { StyleSheet } from 'react-native'

export const styles = StyleSheet.create({
  container: {
    width: '100%',
    height: 40,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 10,
  },
  label: {
    fontSize: 16,
    color: 'rgb(255, 255, 255)',
    width: 70,
    textAlign: 'center',
  },
  slider: {
    flex: 1,
  },
  value: {
    fontSize: 16,
    color: 'rgb(255, 255, 255)',
    width: 40,
    textAlign: 'center',
  },
})

export const minimumTrackTintColor = 'rgb(207, 181, 157)' // White color for the minimum track
export const maximumTrackTintColor = '#CCCCCC' // Light gray color for the maximum track
export const thumbTintColor = 'rgb(201, 141, 62)' // Blue color for the thumb
