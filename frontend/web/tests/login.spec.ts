import { test, expect } from '@playwright/test'
import { mockResponse } from './utils'

test('Invalid Credentials', async ({ page }) => {
  await mockResponse(page, 'login', {
    method: 'POST',
    status: 400,
    contentType: 'application/problem+json',
    body: { title: 'Invalid Login', type: '' },
  })

  // when: navigating to the login page
  await page.goto('http://localhost:3000/login')

  // then: the page 2 inputs and a button
  const usernameInput = page.getByLabel('Username')
  const passwordInput = page.getByLabel('Password')
  const loginButton = page.getByRole('button', { name: 'Login' })

  await expect(usernameInput).toBeVisible()
  await expect(passwordInput).toBeVisible()
  await expect(loginButton).toBeVisible()

  // when: providing incorrect credentials
  await expect(loginButton).toBeDisabled()
  await usernameInput.fill('admin')
  await expect(loginButton).toBeDisabled()
  await passwordInput.fill('123')
  await expect(loginButton).toBeEnabled()

  await loginButton.click()

  // then: the button get disabled
  await expect(loginButton).toBeDisabled()

  // and: the error message appears
  await expect(page.getByText('Invalid Login')).toBeVisible()

  // and: only the username is preserved
  await expect(usernameInput).toHaveValue('admin')
  await expect(passwordInput).toHaveValue('')
})

test('Valid Credentials', async ({ page }) => {
  await mockResponse(page, 'login', {
    method: 'POST',
    status: 200,
    contentType: 'application/json',
    body: { id: 1, username: 'admin', isCoach: true },
  })

  // when: navigating to the login page
  await page.goto('http://localhost:3000/login')

  // then: the page 2 inputs and a button
  const usernameInput = page.getByLabel('Username')
  const passwordInput = page.getByLabel('Password')
  const loginButton = page.getByRole('button', { name: 'Login' })

  await expect(usernameInput).toBeVisible()
  await expect(passwordInput).toBeVisible()
  await expect(loginButton).toBeVisible()

  // when: providing correct credentials
  await expect(loginButton).toBeDisabled()
  await usernameInput.fill('admin')
  await expect(loginButton).toBeDisabled()
  await passwordInput.fill('123')
  await expect(loginButton).toBeEnabled()

  await loginButton.click()

  // then: the button get disabled
  await expect(loginButton).toBeDisabled()

  // and: the user is redirected to the home page
  await expect(page).toHaveURL('http://localhost:3000/')
})

test('Go to Register', async ({ page }) => {
  // when: navigating to the login page
  await page.goto('http://localhost:3000/login')

  // then: the page has a register link
  const link = page.getByRole('link', { name: 'Register' })
  await expect(link).toBeVisible()

  // when: clicking on the register link
  await link.click()

  // then: the user is redirected to the register page
  await expect(page).toHaveURL('http://localhost:3000/register')
})
