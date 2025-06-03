export function handleError(obj: any): string {
  if (obj && typeof obj.title === 'string' && typeof obj.type === 'string') return obj.title

  return 'Operation could not be completed'
}
