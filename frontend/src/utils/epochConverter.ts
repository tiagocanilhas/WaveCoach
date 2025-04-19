export function epochConverter(epoch: number, format: string): string {
  const date = new Date(epoch)

  const map: Record<string, string> = {
    dd: String(date.getDate()).padStart(2, '0'),
    mm: String(date.getMonth() + 1).padStart(2, '0'),
    yyyy: String(date.getFullYear()),
    hh: String(date.getHours()).padStart(2, '0'),
    MM: String(date.getMinutes()).padStart(2, '0'),
    ss: String(date.getSeconds()).padStart(2, '0'),
  }

  let result = format

  for (const token in map) {
    result = result.replace(token, map[token])
  }

  return result
}
