// API модуль для работы с бекендом
const API_BASE_URL = '/api'

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
    
    const response = await fetch(`${API_BASE_URL}/view?${queryParams}`)
    
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
    const response = await fetch(`${API_BASE_URL}/view/${id}`)
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка получения объекта')
    }
    return await response.json()
  },

  // Создать новый объект
  async createCreature(data) {
    const response = await fetch(`${API_BASE_URL}/interact`, {
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
    
    // Бэкенд возвращает строку, а не JSON
    return await response.text()
  },

  // Обновить объект
  async updateCreature(id, data) {
    const response = await fetch(`${API_BASE_URL}/modify/${id}`, {
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
    
    // Бэкенд возвращает строку, а не JSON
    return await response.text()
  },

  // Удалить объект
  async deleteCreature(id) {
    const response = await fetch(`${API_BASE_URL}/delete/${id}`, {
      method: 'DELETE'
    })
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка удаления объекта')
    }
    
    return true
  },

  // Получить города с пагинацией
  async getCities(page = 1) {
    // Конвертируем 1-based в 0-based для Spring
    const backendPage = page - 1
    const response = await fetch(`${API_BASE_URL}/city/view?page=${backendPage}`)
    
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

  // Получить кольца с пагинацией
  async getRings(page = 1) {
    // Конвертируем 1-based в 0-based для Spring
    const backendPage = page - 1
    const response = await fetch(`${API_BASE_URL}/ring/view?page=${backendPage}`)
    
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
  }

}

