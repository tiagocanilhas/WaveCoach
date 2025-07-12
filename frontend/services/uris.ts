const MOBILE_URL = ''
const BASE_URL =
  false
    ? `${MOBILE_URL}/api`
    : '/api';

const USERS_URL = `${BASE_URL}/users`
const USERS = {
  login: `${BASE_URL}/login`,
  logout: `${BASE_URL}/logout`,
  checkAuth: `${BASE_URL}/me`,
  updateUsername: `${BASE_URL}/me/username`,
  updatePassword: `${BASE_URL}/me/password`,
}

const COACHES_URL = `${BASE_URL}/coaches`
const COACHES = {
  create: COACHES_URL,
}

const ATHLETES_URL = `${BASE_URL}/athletes`
const ATHLETES = {
  create: ATHLETES_URL,
  getById: (id: number) => `${ATHLETES_URL}/${id}`,
  getByCoach: ATHLETES_URL,
  update: (id: number) => `${ATHLETES_URL}/${id}`,
  delete: (id: number) => `${ATHLETES_URL}/${id}`,

  generateCode: (id: number) => `${ATHLETES_URL}/${id}/code`,
  getByCode: (code: string) => `${ATHLETES_URL}/code/${code}`,
  changeCredentials: `${ATHLETES_URL}/credentials`,

  createCharacteristics: (id: number) => `${ATHLETES_URL}/${id}/characteristics`,
  getCharacteristics: (id: number, date: string) => `${ATHLETES_URL}/${id}/characteristics/${date}`,
  getCharacteristicsList: (id: number) => `${ATHLETES_URL}/${id}/characteristics`,
  updateCharacteristics: (id: number, date: string) => `${ATHLETES_URL}/${id}/characteristics/${date}`,
  deleteCharacteristics: (id: number, date: string) => `${ATHLETES_URL}/${id}/characteristics/${date}`,

  createCalendar: (id: number) => `${ATHLETES_URL}/${id}/calendar`,
  getCalendar: (id: number, type?: string) => `${ATHLETES_URL}/${id}/calendar?type=${type}`,
  getActivities: (id: number) => `${ATHLETES_URL}/${id}/activities`,

  getWaterActivities: (id: number) => `${ATHLETES_URL}/${id}/water`,
  getLastWaterActivity: (id: number) => `${ATHLETES_URL}/${id}/water/last`,

  createCompetition: (id: number) => `${ATHLETES_URL}/${id}/competition`,
  getCompetition: (aid: number, id: number) => `${ATHLETES_URL}/${aid}/competition/${id}`,
  getCompetitions: (aid: number) => `${ATHLETES_URL}/${aid}/competition`,
  updateCompetition: (id: number, cid: number) => `${ATHLETES_URL}/${id}/competition/${cid}`,
  deleteCompetition: (id: number, cid: number) => `${ATHLETES_URL}/${id}/competition/${cid}`,
}

const GYM_URL = `${BASE_URL}/gym`
const GYM = {
  create: GYM_URL,
  getById: (id: number) => `${GYM_URL}/${id}`,
  update: (id: number) => `${GYM_URL}/${id}`,
  delete: (id: number) => `${GYM_URL}/${id}`,
}

const GYM_EXERCISES_URL = `${BASE_URL}/gym/exercise`
const GYM_EXERCISES = {
  create: GYM_EXERCISES_URL,
  getAll: GYM_EXERCISES_URL,
  getById: (id: number) => `${GYM_EXERCISES_URL}/${id}`,
  update: (id: number) => `${GYM_EXERCISES_URL}/${id}`,
  delete: (id: number) => `${GYM_EXERCISES_URL}/${id}`,
}

const WATER_URL = `${BASE_URL}/water`
const WATER = {
  create: WATER_URL,
  getById: (id: number) => `${WATER_URL}/${id}`,
  update: (id: number) => `${WATER_URL}/${id}`,
  delete: (id: number) => `${WATER_URL}/${id}`,
  
  createQuestionnaire: (id: number) => `${WATER_URL}/${id}/questionnaire`,
  getQuestionnaire: (id: number) => `${WATER_URL}/${id}/questionnaire`,
}

const WATER_MANEUVERS_URL = `${BASE_URL}/water/maneuver`
const WATER_MANEUVERS = {
  create: WATER_MANEUVERS_URL,
  getAll: WATER_MANEUVERS_URL,
  update: (id: number) => `${WATER_MANEUVERS_URL}/${id}`,
  delete: (id: number) => `${WATER_MANEUVERS_URL}/${id}`,
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
