// API модуль для работы с бекендом
import { useAuth } from './composables/useAuth.js'

const API_BASE_URL = '/api'

/**
 * Обёртка для fetch с автоматическим добавлением JWT токена
 */
async function authFetch(url, options = {}) {
  const { getAccessToken, refreshTokens, clearAuth } = useAuth()
  
  const headers = {
    ...options.headers
  }
  
  const token = getAccessToken()
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }
  
  let response = await fetch(url, { ...options, headers })
  
  // Если получили 401, пробуем обновить токен
  if (response.status === 401 && token) {
    const refreshed = await refreshTokens()
    if (refreshed) {
      // Повторяем запрос с новым токеном
      headers['Authorization'] = `Bearer ${getAccessToken()}`
      response = await fetch(url, { ...options, headers })
    } else {
      // Токен не удалось обновить, разлогиниваем
      clearAuth()
      window.location.href = '/'
      throw new Error('Сессия истекла')
    }
  }
  
  return response
}

export const api = {
  // Получить все объекты (с пагинацией и фильтрацией на сервере)
  async getAllCreatures(params = {}) {
    const queryParams = new URLSearchParams()
    
    // Конвертируем 1-based в 0-based для Spring
    const page = (params.page || 1) - 1
    queryParams.append('page', page)
    
    if (params.search) queryParams.append('filter', params.search)
    if (params.sort) queryParams.append('sort', params.sort)
    if (params.direction) queryParams.append('direction', params.direction)
    
    const response = await authFetch(`${API_BASE_URL}/view?${queryParams}`)
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка получения объектов')
    }
    
    const data = await response.json()
    
    // Spring возвращает: { content: [...], totalElements: X, totalPages: Y, number: Z, ... }
    // Конвертируем обратно в 1-based для фронтенда
    return {
      content: data.content,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      currentPage: data.number + 1 // Конвертируем 0-based → 1-based
    }
  },

  // Получить один объект по ID
  async getCreatureById(id) {
    const response = await authFetch(`${API_BASE_URL}/view/${id}`)
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка получения объекта')
    }
    return await response.json()
  },

  // Создать новый объект
  async createCreature(data) {
    const response = await authFetch(`${API_BASE_URL}/interact`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    })
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка создания объекта')
    }
    
    return await response.json()
  },

  // Обновить объект
  async updateCreature(id, data) {
    const response = await authFetch(`${API_BASE_URL}/modify/${id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    })
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка обновления объекта')
    }
    
    return await response.json()
  },

  // Удалить объект
  async deleteCreature(id) {
    const response = await authFetch(`${API_BASE_URL}/delete/${id}`, {
      method: 'DELETE'
    })
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка удаления объекта')
    }
    
    // 204 No Content - успешное удаление
    return true
  },

  // Получить города с пагинацией
  async getCities(page = 1) {
    // Конвертируем 1-based в 0-based для Spring
    const backendPage = page - 1
    const response = await authFetch(`${API_BASE_URL}/city/view?page=${backendPage}`)
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка получения городов')
    }
    
    const data = await response.json()
    
    return {
      content: data.content,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      currentPage: data.number + 1
    }
  },

  // Создать новый город
  async createCity(data) {
    const response = await authFetch(`${API_BASE_URL}/city/interact`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    })
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка создания города')
    }
    
    return await response.json()
  },

  // Получить город по ID
  async getCityById(id) {
    const response = await authFetch(`${API_BASE_URL}/city/view/${id}`)
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка получения города')
    }
    return await response.json()
  },

  // Обновить город
  async updateCity(id, data) {
    const response = await authFetch(`${API_BASE_URL}/city/modify/${id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    })
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка обновления города')
    }
    
    return await response.json()
  },

  // Удалить город
  async deleteCity(id) {
    const response = await authFetch(`${API_BASE_URL}/city/delete/${id}`, {
      method: 'DELETE'
    })
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка удаления города')
    }
    
    return true
  },

  // Получить кольца с пагинацией
  async getRings(page = 1) {
    // Конвертируем 1-based в 0-based для Spring
    const backendPage = page - 1
    const response = await authFetch(`${API_BASE_URL}/ring/view?page=${backendPage}`)
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка получения колец')
    }
    
    const data = await response.json()
    
    return {
      content: data.content,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      currentPage: data.number + 1
    }
  },

  // Создать новое кольцо
  async createRing(data) {
    const response = await authFetch(`${API_BASE_URL}/ring/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    })
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка создания кольца')
    }
    
    return await response.json()
  },

  // Получить кольцо по ID
  async getRingById(id) {
    const response = await authFetch(`${API_BASE_URL}/ring/view/${id}`)
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка получения кольца')
    }
    return await response.json()
  },

  // Обновить кольцо
  async updateRing(id, data) {
    const response = await authFetch(`${API_BASE_URL}/ring/modify/${id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    })
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка обновления кольца')
    }
    
    return await response.json()
  },

  // Удалить кольцо
  async deleteRing(id) {
    const response = await authFetch(`${API_BASE_URL}/ring/delete/${id}`, {
      method: 'DELETE'
    })
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка удаления кольца')
    }
    
    return true
  },

  // Специальные операции
  
  // Подсчет объектов с уровнем защиты больше заданного
  async countByDefenseAbove(value) {
    const response = await authFetch(`${API_BASE_URL}/special/defenseAbove/${value}`)
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка подсчета объектов')
    }
    
    return await response.json()
  },

  // Поиск объектов по началу имени
  async findByNamePrefix(prefix) {
    const encodedPrefix = encodeURIComponent(prefix)
    const response = await authFetch(`${API_BASE_URL}/special/nameMatcher/${encodedPrefix}`)
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка поиска по имени')
    }
    
    return await response.json()
  },

  // Поиск объектов с уровнем защиты меньше заданного
  async findByDefenseBelow(value) {
    const response = await authFetch(`${API_BASE_URL}/special/defenseBelow/${value}`)
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка поиска объектов')
    }
    
    return await response.json()
  },

  // Обмен кольцами между персонажами
  async swapRings(id1, id2) {
    const queryParams = new URLSearchParams()
    queryParams.append('id1', id1)
    queryParams.append('id2', id2)
    const response = await authFetch(`${API_BASE_URL}/special/swap?${queryParams}`, {
      method: 'POST'
    })
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка обмена кольцами')
    }
    
    return await response.json()
  },

  // Переместить хоббитов с кольцами в Мордор
  async moveHobbitsToMordor() {
    const response = await authFetch(`${API_BASE_URL}/special/move`, {
      method: 'POST'
    })
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка перемещения в Мордор')
    }
    
    return await response.json()
  },

  // ========== API для импорта ==========

  // Загрузить YAML файлы для импорта
  async uploadImportFiles(files) {
    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })

    const { getAccessToken } = useAuth()
    const response = await fetch(`${API_BASE_URL}/import/upload`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${getAccessToken()}`
      },
      body: formData
    })
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка загрузки файлов')
    }
    
    return await response.json()
  },

  // Получить историю импорта
  async getImportHistory(page = 0, size = 10) {
    const response = await authFetch(`${API_BASE_URL}/import/history?page=${page}&size=${size}`)
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка получения истории импорта')
    }
    
    return await response.json()
  },

  // Скачать файл с конфликтами
  async downloadConflictFile() {
    const response = await authFetch(`${API_BASE_URL}/import/file`)
    
    if (!response.ok) {
      if (response.status === 404) {
        throw new Error('Файл конфликтов не найден')
      }
      throw new Error('Ошибка скачивания файла')
    }
    
    const blob = await response.blob()
    return blob
  }
}

