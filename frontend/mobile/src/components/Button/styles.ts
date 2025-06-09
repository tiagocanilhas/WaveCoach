export const styles = {
  button: {
    backgroundColor: 'rgb(177, 105, 11)',
    borderRadius: 10,
    paddingVertical: 10,
    paddingHorizontal: 20,
    alignItems: 'center' as const,
  },
  pressed: {
    backgroundColor: 'rgb(189, 134, 17)',
    opacity: 0.9,
  },
  disabled: {
    backgroundColor: 'rgb(100, 100, 100)',
    opacity: 0.5,
  },
  text: {
    color: 'rgb(255, 255, 255)',
    fontSize: 20,
    fontWeight: 'bold' as const,
  },
}
