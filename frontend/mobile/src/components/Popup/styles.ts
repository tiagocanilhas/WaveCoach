import { StyleSheet } from 'react-native'

export const styles = StyleSheet.create({
  overlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.69)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  closeButton: {
    position: 'absolute',
    top: 10,
    right: 10,
    padding: 8,
    zIndex: 1,
  },
  container: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0)',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 10,
  },
  popup: {
    width: '80%',
    paddingTop: 20,
    backgroundColor: '#fff',
    borderRadius: 10,
    alignItems: 'center',
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  content: {
    marginBottom: 20,
  },
})
