export function validatePassword(password: string) {
  return {
    length: {
      valid: password.length > 6,
      text: 'At least 7 characters',
    },
    uppercase: {
      valid: /[A-Z]/.test(password),
      text: 'At least 1 uppercase letter',
    },
    lowercase: {
      valid: /[a-z]/.test(password),
      text: 'At least 1 lowercase letter',
    },
    number: {
      valid: /\d/.test(password),
      text: 'At least 1 number',
    },
    specialChar: {
      valid: /[!@#$%^&*(),.?":{}|<>]/.test(password),
      text: 'At least 1 special character (e.g., @, #, $, etc.)',
    },
  }
}
