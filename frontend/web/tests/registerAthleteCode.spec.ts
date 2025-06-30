import { test, expect } from '@playwright/test'
import { mockResponse } from './utils'

test('Athlete Code Check', async ({ page }) => {
  await mockResponse(page, 'athletes/code/*', {
    status: 200,
    contentType: 'application/json',
    body: { username: 'athlete' },
  })

  // when: navigating to the athlete registration page
  await page.goto('http://localhost:3000/register/athlete-code')

  // then: the page has a form with inputs and a button
  const input = page.getByLabel('Code')
  const button = page.getByRole('button', { name: 'Check' })

  await expect(input).toBeVisible()
  await expect(button).toBeVisible()

  // when: filling the form with valid data
  await expect(button).toBeDisabled()
  await input.fill('code')
  await expect(button).toBeEnabled()

  // when: clicking the button
  await button.click()

  // then: the user is redirected to the athlete registration page
  await expect(page).toHaveURL('http://localhost:3000/register/athlete?code=code')
})
