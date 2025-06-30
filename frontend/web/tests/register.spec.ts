import { test, expect } from '@playwright/test'

test('Choose Coach', async ({ page }) => {
  // when: navigating to the register page
  await page.goto('http://localhost:3000/register')

  // then: the page has 2 buttons
  const coach = page.getByRole('button', { name: 'Coach' })
  const athlete = page.getByRole('button', { name: 'Athlete' })

  await expect(coach).toBeVisible()
  await expect(athlete).toBeVisible()

  // when: clicking on the coach button
  await coach.click()

  // then: the user is redirected to the coach registration page
  await expect(page).toHaveURL('http://localhost:3000/register/coach')
})

test('Choose Athlete', async ({ page }) => {
  // when: navigating to the register page
  await page.goto('http://localhost:3000/register')

  // then: the page has 2 buttons
  const coach = page.getByRole('button', { name: 'Coach' })
  const athlete = page.getByRole('button', { name: 'Athlete' })

  await expect(coach).toBeVisible()
  await expect(athlete).toBeVisible()

  // when: clicking on the athlete button
  await athlete.click()

  // then: the user is redirected to the athlete registration page
  await expect(page).toHaveURL('http://localhost:3000/register/athlete-code')
})

test('Go to Login', async ({ page }) => {
  // when: navigating to the register page
  await page.goto('http://localhost:3000/register')

  // then: the page has a login link
  const link = page.getByRole('link', { name: 'Login' })
  await expect(link).toBeVisible()

  // when: clicking on the login link
  await link.click()

  // then: the user is redirected to the login page
  await expect(page).toHaveURL('http://localhost:3000/login')
})
