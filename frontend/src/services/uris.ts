const BASE_URL = '/api'

const USERS_URL = `${BASE_URL}/users`
const USERS = {
  login: `${BASE_URL}/login`,
  logout: `${BASE_URL}/logout`,
  create: USERS_URL,
}

const ATHLETES_URL = `${BASE_URL}/athletes`
const ATHLETES = {
  create: ATHLETES_URL,
  getById: (id: string) => `${ATHLETES_URL}/${id}`,
  getByCoach: ATHLETES_URL,
  update: (id: string) => `${ATHLETES_URL}/${id}`,
  delete: (id: string) => `${ATHLETES_URL}/${id}`,

  createCharacteristics: (id: string) => `${ATHLETES_URL}/${id}/characteristics`,
  getCharacteristics: (id: string, date: string) => `${ATHLETES_URL}/${id}/characteristics/${date}`,
  getCharacteristicsList: (id: string) => `${ATHLETES_URL}/${id}/characteristics`,
  updateCharacteristics: (id: string, date: string) => `${ATHLETES_URL}/${id}/characteristics/${date}`,
  deleteCharacteristics: (id: string, date: string) => `${ATHLETES_URL}/${id}/characteristics/${date}`,

  createGymActivity: (id: string) => `${ATHLETES_URL}/${id}/gym`,
}

export const URIS = {
  USERS,
  ATHLETES,
}
