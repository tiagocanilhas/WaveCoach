import { StyleSheet } from 'react-native';

export const styles = StyleSheet.create({
    container: {
    flexDirection: 'row',
    alignItems: 'center',
    width: '100%',
    height: '6%',
    paddingHorizontal: 5,
    backgroundColor: '#FFFFFF',
    borderBottomWidth: 1,
    borderBottomColor: '#E0E0E0',
    },
  backButton: {
    padding: 2,
  },
backButtonColor: "rgb(177, 134, 42)" as any,
  titleContainer: {
    flex: 1,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    color: "rgb(177, 134, 42)",
  },
})