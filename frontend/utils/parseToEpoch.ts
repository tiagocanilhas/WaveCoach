export function parseToEpoch(dateString: string, format: string): number {
  const formatParts = format.match(/(dd|mm|yyyy|hh|MM|ss)/g)
  const dateParts = dateString.match(/\d+/g)

  if (!formatParts || !dateParts || formatParts.length !== dateParts.length) {
    throw new Error('Invalid date or format')
  }

  const components: Record<string, number> = {
    dd: 1,
    mm: 1,
    yyyy: 1970,
    hh: 0,
    MM: 0,
    ss: 0,
  }

  formatParts.forEach((part, i) => {
    components[part] = Number(dateParts[i])
  })

  const date = new Date(components.yyyy, components.mm - 1, components.dd, components.hh, components.MM, components.ss)

  return date.getTime()
}
