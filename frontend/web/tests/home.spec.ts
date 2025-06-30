import { test, expect } from '@playwright/test'
import { mockResponse, mockMultipleResponses } from './utils'

test('Content', async ({ page }) => {
  await mockResponse(page, 'me', {
    status: 200,
    contentType: 'application/json',
    body: { id: 1, username: 'admin', isCoach: true },
  })

  await mockResponse(page, 'athletes', {
    status: 200,
    contentType: 'application/json',
    body: {
      athletes: [
        { uid: 1, coach: 1, name: 'Athlete 1', birthdate: 1720000000, credentialsChanged: true },
        { uid: 2, coach: 1, name: 'Athlete 2', birthdate: 1720000000, credentialsChanged: false },
        { uid: 3, coach: 1, name: 'Athlete 3', birthdate: 1720000000, credentialsChanged: true },
      ],
    },
  })

  // when: navigating to the home page
  await page.goto('http://localhost:3000/')

  // then: the page should display the title, search input, add button, and the athletes
  const title = page.getByRole('heading', { name: 'Wave Coach' })
  const searchInput = page.getByLabel('Search')
  const addButton = page.getByText('+')
  const athlete = page.getByTestId('athlete-1')
  const drowpdown = athlete.getByTestId('dropdown')
  const athleteName = athlete.getByTestId('scrollable-text')

  await expect(title).toBeVisible()
  await expect(searchInput).toBeVisible()
  await expect(addButton).toBeVisible()
  await expect(athlete).toBeVisible()
  await expect(drowpdown).toBeVisible()
  await expect(athleteName).toHaveText('Athlete 1')
})

test('Search', async ({ page }) => {
  await mockResponse(page, 'me', {
    status: 200,
    contentType: 'application/json',
    body: { id: 1, username: 'admin', isCoach: true },
  })

  await mockResponse(page, 'athletes', {
    status: 200,
    contentType: 'application/json',
    body: {
      athletes: [
        { uid: 1, coach: 1, name: 'Athlete 1', birthdate: 1720000000, credentialsChanged: true },
        { uid: 2, coach: 1, name: 'Athlete 2', birthdate: 1720000000, credentialsChanged: false },
      ],
    },
  })

  // when: navigating to the home page
  await page.goto('http://localhost:3000/')

  // then: the page should display search input and the athletes
  const searchInput = page.getByLabel('Search')
  const athlete1 = page.getByTestId('athlete-1')
  const athlete2 = page.getByTestId('athlete-2')

  // when: searching for "Athlete 1"
  await searchInput.fill('Athlete 1')

  // then only Athlete 1 should be visible

  await expect(athlete1).toBeVisible()
  await expect(athlete2).not.toBeVisible()
})

test('Add Athlete', async ({ page }) => {
  await mockResponse(page, 'me', {
    status: 200,
    contentType: 'application/json',
    body: { id: 1, username: 'admin', isCoach: true },
  })

  await mockMultipleResponses(page, 'athletes', [
    {
      status: 200,
      contentType: 'application/json',
      body: {
        athletes: [
          { uid: 1, coach: 1, name: 'Athlete 1', birthdate: 1720000000, credentialsChanged: true },
          { uid: 2, coach: 1, name: 'Athlete 2', birthdate: 1720000000, credentialsChanged: false },
        ],
      },
    },
    {
      status: 200,
      contentType: 'application/json',
      body: {
        athletes: [
          { uid: 1, coach: 1, name: 'Athlete 1', birthdate: 1720000000, credentialsChanged: true },
          { uid: 2, coach: 1, name: 'Athlete 2', birthdate: 1720000000, credentialsChanged: false },
          { uid: 3, coach: 1, name: 'New Athlete', birthdate: 1720000000, credentialsChanged: true },
        ],
      },
    },
  ])

  await mockResponse(page, 'athletes', {
    method: 'POST',
    status: 201,
    contentType: 'application/json',
    body: {},
  })

  // when: navigating to the home page
  await page.goto('http://localhost:3000/')

  // then: the page should display the add button
  const addButton = page.getByText('+')
  await expect(addButton).toBeVisible()

  // when: clicking the add button
  await addButton.click()

  // then: the popup should be visible
  const popup = page.getByTestId('popup')
  await expect(popup).toBeVisible()

  // then: the popup should display close button, the title, inputs, image selector and button
  const closeButton = popup.getByRole('button', { name: 'X' })
  const title = page.getByText('Add Athlete')
  const nameInput = popup.getByLabel('Name')
  const birthdateInput = popup.getByLabel('Birthdate')
  const imageSelector = popup.getByTestId('image-selector')
  const addButtonPopup = popup.getByRole('button', { name: 'Add' })

  await expect(closeButton).toBeVisible()
  await expect(title).toBeVisible()
  await expect(nameInput).toBeVisible()
  await expect(birthdateInput).toBeVisible()
  await expect(imageSelector).toBeVisible()
  await expect(addButtonPopup).toBeVisible()

  //when: filling the inputs
  await expect(addButtonPopup).toBeDisabled()
  await nameInput.fill('New Athlete')
  await birthdateInput.fill('2000-01-01')

  // then: the add button should be enabled
  await expect(addButtonPopup).toBeEnabled()

  // when: clicking the add button
  await addButtonPopup.click()

  // then: the popup should be closed and the new athlete should be visible
  await expect(popup).not.toBeVisible()
  const newAthlete = page.getByTestId('athlete-3')
  await expect(newAthlete).toBeVisible()
})

test('Athlete Dropdown', async ({ page }) => {
  await mockResponse(page, 'me', {
    status: 200,
    contentType: 'application/json',
    body: { id: 1, username: 'admin', isCoach: true },
  })

  await mockMultipleResponses(page, 'athletes', [
    {
      status: 200,
      contentType: 'application/json',
      body: {
        athletes: [
          { uid: 1, coach: 1, name: 'Athlete 1', birthdate: 1720000000, credentialsChanged: true },
          { uid: 2, coach: 1, name: 'Athlete 2', birthdate: 1720000000, credentialsChanged: false },
        ],
      },
    },
    {
      status: 200,
      contentType: 'application/json',
      body: {
        athletes: [{ uid: 1, coach: 1, name: 'Athlete 1', birthdate: 1720000000, credentialsChanged: true }],
      },
    },
  ])

  await mockResponse(page, 'athletes/2/code', {
    method: 'POST',
    status: 200,
    contentType: 'application/json',
    body: { code: 'code' },
  })

  // await mockResponse(page, 'athletes/2', {
  //     method: 'DELETE',
  //     status: 204,
  //     contentType: 'application/json',
  //     body: {},
  // })

  // when: navigating to the home page
  await page.goto('http://localhost:3000/')

  // then: the page should display the athletes and their dropdowns
  const athlete1 = page.getByTestId('athlete-1')
  const athlete2 = page.getByTestId('athlete-2')
  const athlete1Dropdown = athlete1.getByTestId('dropdown')
  const athlete2Dropdown = athlete2.getByTestId('dropdown')

  await expect(athlete1).toBeVisible()
  await expect(athlete2).toBeVisible()
  await expect(athlete1Dropdown).toBeVisible()
  await expect(athlete2Dropdown).toBeVisible()

  // when: clicking the dropdown of Athlete 1
  await athlete1Dropdown.click()

  // then: the dropdown should display the options
  const athlete1DropdownOptions = athlete1Dropdown.getByRole('listitem')
  await expect(athlete1DropdownOptions).toHaveCount(1)
  await expect(athlete1DropdownOptions.nth(0)).toHaveText('Delete')

  // when: clicking the dropdown of Athlete 2
  await athlete2Dropdown.click()

  // then: the dropdown should display the options
  const athlete2DropdownOptions = athlete2Dropdown.getByRole('listitem')
  await expect(athlete2DropdownOptions).toHaveCount(2)
  const athlete2GenerateCodeOption = athlete2DropdownOptions.nth(0)
  const athlete2DeleteOption = athlete2DropdownOptions.nth(1)
  await expect(athlete2GenerateCodeOption).toHaveText('Generate Code')
  await expect(athlete2DeleteOption).toHaveText('Delete')

  // when: clicking the generate code option of Athlete 2
  await athlete2GenerateCodeOption.click()

  // then: an alert should be displayed with the message "Copied to clipboard"
  page.on('dialog', async dialog => {
    expect(dialog.type()).toBe('alert')
    expect(dialog.message()).toBe('Code was copied to clipboard!')
    await dialog.accept()
  })

  // when: clicking the dropdown of Athlete 2 again
  await athlete2Dropdown.click()

  // then: an confirm dialog should be displayed
  page.on('dialog', async dialog => {
    expect(dialog.type()).toBe('confirm')
    expect(dialog.message()).toBe('Are you sure you want to delete this athlete?')
    await dialog.accept()
  })

  // then: click the delete option
  await athlete2DeleteOption.click()

  // then: the athlete should be deleted and only Athlete 1 should be visible
  await expect(athlete1).toBeVisible()
  await expect(athlete2).not.toBeVisible()
})
