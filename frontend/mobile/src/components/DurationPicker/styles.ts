import { StyleSheet } from 'react-native'

export const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    justifyContent: 'space-evenly',
    gap: 20,
  },
  inputContainer: {
    gap: 10,
    alignItems: 'center',
  },
  label: {
    fontSize: 16,
    fontWeight: 'bold',
    color: 'rgb(255, 255, 255), 0.7)',
  },
  wheelPickerContainer: {
    height: 100,
    overflow: 'hidden',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 10,
  },
  wheelPicker: {
    // Hexcolor and have 7 length (e.g.#B38A5C)
    color: '#B38A5C',
    width: 100,
    height: 200,
  },
})
