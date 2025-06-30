import { Page, Route } from '@playwright/test'

type method = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH' | 'OPTIONS' | 'HEAD'

export async function mockResponse(
  page: Page,
  endpointPattern: string,
  options: {
    method?: method
    status: number
    contentType: string
    body: any
  }
) {
  await page.route(`**/api/${endpointPattern}`, async (route: Route) => {
    const method = options.method ?? 'GET'
    if (route.request().method() === method) {
      await new Promise(resolve => setTimeout(resolve, 500))
      await route.fulfill({
        status: options.status,
        contentType: options.contentType,
        body: JSON.stringify(options.body),
      })
    } else {
      await route.fallback()
    }
  })
}

export async function mockMultipleResponses(
  page: Page,
  endpointPattern: string,
  optionsArray: Array<{
    method?: method
    status: number
    contentType: string
    body: any
  }>
) {
  let callCount = 0

  await page.route(`**/api/${endpointPattern}`, async (route: Route) => {
    const currentOptions = optionsArray[Math.min(callCount, optionsArray.length - 1)]
    callCount++

    const method = currentOptions.method ?? 'GET'
    if (route.request().method() === method) {
      await new Promise(resolve => setTimeout(resolve, 500))
      await route.fulfill({
        status: currentOptions.status,
        contentType: currentOptions.contentType,
        body: JSON.stringify(currentOptions.body),
      })
    } else {
      await route.fallback()
    }
  })
}
