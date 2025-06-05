const BASE_URL = '/api'

const USERS_URL = `${BASE_URL}/users`
const USERS = {
  login: `${BASE_URL}/login`,
  logout: `${BASE_URL}/logout`,
  checkAuth: `${BASE_URL}/me`,
  update: `${BASE_URL}/me`,
}

const COACHES_URL = `${BASE_URL}/coaches`
const COACHES = {
  create: COACHES_URL,
}

const ATHLETES_URL = `${BASE_URL}/athletes`
const ATHLETES = {
  create: ATHLETES_URL,
  getById: (id: string) => `${ATHLETES_URL}/${id}`,
  getByCoach: ATHLETES_URL,
  update: (id: string) => `${ATHLETES_URL}/${id}`,
  delete: (id: string) => `${ATHLETES_URL}/${id}`,

  generateCode: (id: string) => `${ATHLETES_URL}/${id}/code`,
  getByCode: (code: string) => `${ATHLETES_URL}/code/${code}`,
  changeCredentials: `${ATHLETES_URL}/credentials`,

  createCharacteristics: (id: string) => `${ATHLETES_URL}/${id}/characteristics`,
  getCharacteristics: (id: string, date: string) => `${ATHLETES_URL}/${id}/characteristics/${date}`,
  getCharacteristicsList: (id: string) => `${ATHLETES_URL}/${id}/characteristics`,
  updateCharacteristics: (id: string, date: string) => `${ATHLETES_URL}/${id}/characteristics/${date}`,
  deleteCharacteristics: (id: string, date: string) => `${ATHLETES_URL}/${id}/characteristics/${date}`,

  createCalendar: (id: string) => `${ATHLETES_URL}/${id}/calendar`,
  getCalendar: (id: string, type: string) => `${ATHLETES_URL}/${id}/calendar?type=${type}`,
  getActivities: (id: string) => `${ATHLETES_URL}/${id}/activities`,

  getWaterActivities: (id: string) => `${ATHLETES_URL}/${id}/water`,
}

const GYM_URL = `${BASE_URL}/gym`
const GYM = {
  create: GYM_URL,
  getById: (id: string) => `${GYM_URL}/${id}`,
  update: (id: string) => `${GYM_URL}/${id}`,
  delete: (id: string) => `${GYM_URL}/${id}`,
}

const GYM_EXERCISES_URL = `${BASE_URL}/gym/exercise`
const GYM_EXERCISES = {
  create: GYM_EXERCISES_URL,
  getAll: GYM_EXERCISES_URL,
  getById: (id: string) => `${GYM_EXERCISES_URL}/${id}`,
  update: (id: string) => `${GYM_EXERCISES_URL}/${id}`,
  delete: (id: string) => `${GYM_EXERCISES_URL}/${id}`,
}

const WATER_URL = `${BASE_URL}/water`
const WATER = {
  create: WATER_URL,
  getById: (id: string) => `${WATER_URL}/${id}`,
  update: (id: string) => `${WATER_URL}/${id}`,
  delete: (id: string) => `${WATER_URL}/${id}`,
  
  createQuestionnaire: (id: string) => `${WATER_URL}/${id}/questionnaire`,
  getQuestionnaire: (id: string) => `${WATER_URL}/${id}/questionnaire`,
}

const WATER_MANEUVERS_URL = `${BASE_URL}/water/maneuver`
const WATER_MANEUVERS = {
  create: WATER_MANEUVERS_URL,
  getAll: WATER_MANEUVERS_URL,
  update: (id: string) => `${WATER_MANEUVERS_URL}/${id}`,
  delete: (id: string) => `${WATER_MANEUVERS_URL}/${id}`,
}

export const URIS = {
  USERS,
  COACHES,
  ATHLETES,
  GYM,
  GYM_EXERCISES,
  WATER,
  WATER_MANEUVERS,
}
