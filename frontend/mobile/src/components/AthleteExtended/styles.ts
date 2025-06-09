import { StyleSheet } from 'react-native'

export const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    width: '100%',
    borderRadius: 10,
    borderColor: 'rgb(255, 255, 255)',
    borderWidth: 1,
    padding: 5,
    backgroundColor: 'rgb(255, 255, 255), 0.5)',
    alignItems: 'center',
  },
  imageContainer: {
    flex: 0.4,
    aspectRatio: 1,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 10,
  },
  image: {
    width: '100%',
    height: '100%',
    borderRadius: 10,
    marginBottom: 5,
  },
  infoContainer: {
    flex: 0.6,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 5,
    gap: 10,
  },
  text: {
    fontSize: 18,
    fontWeight: 'bold',
    textAlign: 'center',
    overflow: 'hidden',
  },
})
