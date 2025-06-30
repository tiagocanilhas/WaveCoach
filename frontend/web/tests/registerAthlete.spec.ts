import { test, expect } from '@playwright/test'
import { mockResponse } from './utils'

test('Check Invalid Athlete Code', async ({ page }) => {
  await mockResponse(page, 'athlete/code/invalid', {
    status: 400,
    contentType: 'application/problem+json',
    body: { title: '', type: '' },
  })

  // when: navigating to the athlete registration page
  await page.goto('http://localhost:3000/register/athlete')

  // then: is redirected to the athlete code page
  await expect(page).toHaveURL('http://localhost:3000/register/athlete-code')
})

test('Fill Athlete Registration Form', async ({ page }) => {
  await mockResponse(page, 'athletes/code/*', {
    status: 200,
    contentType: 'application/json',
    body: { username: 'athlete' },
  })

  // when: navigating to the coach registration page
  await page.goto('http://localhost:3000/register/athlete?code=code')

  // then: the page has a form with inputs and a button
  const usernameInput = page.getByLabel('Username')
  const passwordInput = page.locator('input[name="password"]')
  const confirmPasswordInput = page.locator('input[name="confirmPassword"]')
  const button = page.getByRole('button', { name: 'Change Credentials' })

  await expect(usernameInput).toBeVisible()
  await expect(usernameInput).toHaveValue('athlete')
  await expect(passwordInput).toBeVisible()
  await expect(confirmPasswordInput).toBeVisible()
  await expect(button).toBeVisible()

  //when: filling the password input incorrectly; then: the password input is valid
  await expect(button).toBeDisabled()
  await expect(passwordInput).toHaveAttribute('aria-invalid', 'false')
  await passwordInput.fill('Abc12!')
  await expect(passwordInput).toHaveAttribute('aria-invalid', 'true')
  await passwordInput.fill('abc123!')
  await expect(passwordInput).toHaveAttribute('aria-invalid', 'true')
  await passwordInput.fill('ABC123!')
  await expect(passwordInput).toHaveAttribute('aria-invalid', 'true')
  await passwordInput.fill('Abcdef!')
  await expect(passwordInput).toHaveAttribute('aria-invalid', 'true')
  await passwordInput.fill('Abc1234')
  await expect(passwordInput).toHaveAttribute('aria-invalid', 'true')
  await expect(button).toBeDisabled()

  //when: filling the password input correctly; then: the password input is valid
  await expect(button).toBeDisabled()
  await passwordInput.fill('Abc123!')
  await expect(passwordInput).toHaveAttribute('aria-invalid', 'false')
  await expect(button).toBeDisabled()

  //when: filling the confirm password input incorrectly; then: the confirm password input is valid
  await expect(button).toBeDisabled()
  await expect(confirmPasswordInput).toHaveAttribute('aria-invalid', 'false')
  await confirmPasswordInput.fill('not the same')
  await expect(confirmPasswordInput).toHaveAttribute('aria-invalid', 'true')
  await expect(button).toBeDisabled()

  //when: filling the confirm password input correctly; then: the confirm password input is valid
  await expect(button).toBeDisabled()
  await confirmPasswordInput.fill('Abc123!')
  await expect(confirmPasswordInput).toHaveAttribute('aria-invalid', 'false')

  // then: the button is enabled
  await expect(button).toBeEnabled()
})

test('Submit Athlete Registration error', async ({ page }) => {
  await mockResponse(page, 'athletes/code/*', {
    status: 200,
    contentType: 'application/json',
    body: { username: 'athlete' },
  })

  await mockResponse(page, 'athletes/credentials', {
    method: 'POST',
    status: 400,
    contentType: 'application/problem+json',
    body: { title: 'Invalid Registration', type: '' },
  })

  // when: navigating to the coach registration page
  await page.goto('http://localhost:3000/register/athlete?code=code')

  // then: the page has a form with inputs and a button
  const usernameInput = page.getByLabel('Username')
  const passwordInput = page.locator('input[name="password"]')
  const confirmPasswordInput = page.locator('input[name="confirmPassword"]')
  const button = page.getByRole('button', { name: 'Change Credentials' })

  await expect(usernameInput).toBeVisible()
  await expect(usernameInput).toHaveValue('athlete')
  await expect(passwordInput).toBeVisible()
  await expect(confirmPasswordInput).toBeVisible()
  await expect(button).toBeVisible()

  // when: filling the form with valid dat
  await passwordInput.fill('Abc123!')
  await confirmPasswordInput.fill('Abc123!')

  await expect(button).toBeEnabled()

  // then: button is enabled
  await expect(button).toBeEnabled()

  // when: clicking the button
  await button.click()

  // then: the button gets disabled
  await expect(button).toBeDisabled()

  // and: the error message appears
  await expect(page.getByText('Invalid Registration')).toBeVisible()
})
