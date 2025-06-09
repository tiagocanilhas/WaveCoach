import { StyleSheet } from 'react-native'

export const styles = StyleSheet.create({
  container: {
    width: '100%',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: 'rgb(255, 255, 255)',
  },
  header: {
    flexDirection: 'row', 
    justifyContent: 'space-between', 
    alignItems: 'center', 
    padding: 10,
    gap: 10,
  },
  input: {
    width: '80%',
  },
  button: { 
    padding: 10,
    backgroundColor: 'lightgray',
    borderRadius: 5,
    alignItems: 'center',
  },
  scrollContainer: {
    paddingHorizontal: 20,
    width: '100%',
  },
  athletesContainer: {
    flexWrap: 'wrap',
    justifyContent: 'center',
    gap: 10,
  },
  extended: {
    flexDirection: 'column',
  },
  notExtended: {
    flexDirection: 'row',
  },
})
