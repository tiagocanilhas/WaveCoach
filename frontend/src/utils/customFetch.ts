function isMediaTypeProblem(res: Response): boolean {
  return res.headers.get('Content-Type')?.includes('application/problem+json') ?? false
}

type method = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH' | 'OPTIONS' | 'HEAD'

export async function customFetch(url: string, method: method, body?: object): Promise<any> {
  const options: RequestInit = { method: method }

  if (body) {
    options.headers = { 'Content-Type': 'application/json' }
    options.body = JSON.stringify(body)
  }

  const res = await fetch(url, options)

  const json = await res.json()
  if (isMediaTypeProblem(res)) throw json

  return json
}
