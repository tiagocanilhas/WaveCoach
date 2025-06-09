import * as SecureStore from 'expo-secure-store'

type Credentials = {
  username: string
  password: string
}

export class Storage {
  private static TOKEN_KEY = 'token'
  private static USERNAME_KEY = 'username'
  private static PASSWORD_KEY = 'password'

  static async saveToken(token: string): Promise<void> {
    await SecureStore.setItemAsync(this.TOKEN_KEY, token)
  }

  static async saveCredentials({ username, password }: Credentials): Promise<void> {
    await SecureStore.setItemAsync(this.USERNAME_KEY, username)
    await SecureStore.setItemAsync(this.PASSWORD_KEY, password)
  }

  static async getToken(): Promise<string | null> {
    return await SecureStore.getItemAsync(this.TOKEN_KEY)
  }

  static async getCredentials(): Promise<Credentials | null> {
    const username = await SecureStore.getItemAsync(this.USERNAME_KEY)
    const password = await SecureStore.getItemAsync(this.PASSWORD_KEY)

    if (!username || !password) return null
    return { username, password }
  }

  static async deleteToken(): Promise<void> {
    await SecureStore.deleteItemAsync(this.TOKEN_KEY)
  }

  static async deleteCredentials(): Promise<void> {
    await SecureStore.deleteItemAsync(this.USERNAME_KEY)
    await SecureStore.deleteItemAsync(this.PASSWORD_KEY)
  }

  static async clearAll(): Promise<void> {
    await this.deleteToken()
    await this.deleteCredentials()
  }
}
