export function epochConverterToAge(time: number): number {
  const birthdate = new Date(time)
  const today = new Date()
  const age = today.getFullYear() - birthdate.getFullYear()
  const monthDiff = today.getMonth() - birthdate.getMonth()

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthdate.getDate())) return age - 1

  return age
}
