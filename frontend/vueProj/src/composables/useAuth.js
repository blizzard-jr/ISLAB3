import { ref, computed } from 'vue'

// Реактивное состояние аутентификации
const accessToken = ref(localStorage.getItem('accessToken') || null)
const refreshToken = ref(localStorage.getItem('refreshToken') || null)
const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

export function useAuth() {
  const isAuthenticated = computed(() => !!accessToken.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const username = computed(() => user.value?.username || '')

  /**
   * Сохранить токены и данные пользователя
   */
  function setAuth(authData, userData = null) {
    accessToken.value = authData.accessToken
    refreshToken.value = authData.refreshToken
    localStorage.setItem('accessToken', authData.accessToken)
    localStorage.setItem('refreshToken', authData.refreshToken)
    
    if (userData) {
      user.value = userData
      localStorage.setItem('user', JSON.stringify(userData))
    }
  }

  /**
   * Очистить данные аутентификации
   */
  function clearAuth() {
    accessToken.value = null
    refreshToken.value = null
    user.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
  }

  /**
   * Получить access token для запросов
   */
  function getAccessToken() {
    return accessToken.value
  }

  /**
   * Получить refresh token
   */
  function getRefreshToken() {
    return refreshToken.value
  }

  /**
   * Обновить токены через refresh token
   */
  async function refreshTokens() {
    if (!refreshToken.value) {
      clearAuth()
      return false
    }

    try {
      const response = await fetch('/refresh_token', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${refreshToken.value}`
        }
      })

      if (!response.ok) {
        clearAuth()
        return false
      }

      const data = await response.json()
      setAuth(data, user.value)
      return true
    } catch (error) {
      console.error('Failed to refresh token:', error)
      clearAuth()
      return false
    }
  }

  /**
   * Регистрация нового пользователя
   */
  async function register(usernameVal, password, role = 'USER') {
    const response = await fetch('/registration', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username: usernameVal, password, role })
    })

    if (!response.ok) {
      if (response.status === 409) {
        throw new Error('Пользователь с таким именем уже существует. Попробуйте войти.')
      }
      if (response.status === 400) {
        throw new Error('Неверные данные для регистрации')
      }
      throw new Error('Ошибка регистрации')
    }

    const text = await response.text()
    if (!text) {
      throw new Error('Пустой ответ от сервера')
    }
    
    const data = JSON.parse(text)
    setAuth(data, { username: usernameVal, role })
    return data
  }

  /**
   * Вход в систему
   */
  async function login(username, password) {
    const response = await fetch('/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username, password })
    })

    if (!response.ok) {
      if (response.status === 401) {
        throw new Error('Неверное имя пользователя или пароль')
      }
      throw new Error('Ошибка входа')
    }

    const data = await response.json()
    // Декодируем JWT для получения информации о пользователе
    const payload = parseJwt(data.accessToken)
    setAuth(data, { username: payload.sub, role: 'USER' }) // роль нужно получать с сервера
    return data
  }

  /**
   * Выход из системы
   */
  async function logout() {
    try {
      await fetch('/logout', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${accessToken.value}`
        }
      })
    } catch (error) {
      console.error('Logout error:', error)
    }
    clearAuth()
  }

  /**
   * Декодирование JWT токена
   */
  function parseJwt(token) {
    try {
      const base64Url = token.split('.')[1]
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      )
      return JSON.parse(jsonPayload)
    } catch (e) {
      return {}
    }
  }

  return {
    isAuthenticated,
    isAdmin,
    username,
    user,
    getAccessToken,
    getRefreshToken,
    setAuth,
    clearAuth,
    refreshTokens,
    register,
    login,
    logout
  }
}
