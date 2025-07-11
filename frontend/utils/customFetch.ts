function isMediaTypeProblem(res: Response): boolean {
  return res.headers.get('Content-Type')?.includes('application/problem+json') ?? false
}

type method = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH' | 'OPTIONS' | 'HEAD'

export async function customFetch(
  url: string,
  method: method,
  body?: object,
  token?: string
): Promise<any> {
  const options: RequestInit = { method: method }

  if (token) {
    options.headers = {
      ...(options.headers || {}),
      'Authorization': `Bearer ${token}`,
    };
  }

  if (body instanceof FormData) {
    options.body = body
  } else if (body) {
    options.headers = {
      ...(options.headers || {}),
      'Content-Type': 'application/json',
    };
    options.body = JSON.stringify(body)
  }

  // await new Promise((resolve) => { setTimeout(resolve, 1000) })
  const res = await fetch(url, options)

  const text = await res.text()
  if (text === '') return null

  const json = JSON.parse(text)
  if (isMediaTypeProblem(res)) throw { status: res.status,  res: json}

  return { status: res.status,  res: json}
}
