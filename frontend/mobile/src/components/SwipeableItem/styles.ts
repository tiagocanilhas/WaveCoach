import { StyleSheet } from 'react-native'

export const styles = StyleSheet.create({
  item: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    padding: 20,
    borderRadius: 5,
  },
  deleteButton: {
    backgroundColor: 'red',
    justifyContent: 'center',
    alignItems: 'flex-end',
    padding: 20,
    borderRadius: 5,
  },
  deleteText: {
    color: '#fff',
    fontWeight: 'bold',
  },
})
