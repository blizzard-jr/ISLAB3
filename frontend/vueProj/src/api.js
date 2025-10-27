// API модуль для работы с бекендом
const API_BASE_URL = '/api'

export const api = {
  // Получить все объекты (с пагинацией и фильтрацией)
  async getAllCreatures(params = {}) {
    // TODO: Реализовать когда будет ручка на бекенде
    const queryParams = new URLSearchParams()
    if (params.page) queryParams.append('page', params.page)
    if (params.size) queryParams.append('size', params.size)
    if (params.search) queryParams.append('search', params.search)
    if (params.sort) queryParams.append('sort', params.sort)
    
    // Временная заглушка
    return {
      content: [
        {
          id: 1,
          name: 'Фродо Бэггинс',
          coordinates: { x: 10, y: 20.5 },
          creationDate: new Date().toISOString(),
          age: 50,
          creatureType: 'HOBBIT',
          creatureLocation: {
            name: 'Шир',
            area: 1000,
            population: 5000,
            establishmentDate: '2024-01-01T00:00:00',
            governor: 'HOBBIT',
            capital: true,
            populationDensity: 5
          },
          attackLevel: 15.5,
          defenseLevel: 25.0,
          ring: {
            name: 'Единое Кольцо',
            power: 9999,
            weight: 0.5
          }
        },
        {
          id: 2,
          name: 'Леголас',
          coordinates: { x: 100, y: 150.3 },
          creationDate: new Date().toISOString(),
          age: 2931,
          creatureType: 'ELF',
          creatureLocation: null,
          attackLevel: 95.0,
          defenseLevel: 80.0,
          ring: null
        }
      ],
      totalElements: 2,
      totalPages: 1
    }
  },

  // Получить один объект по ID
  async getCreatureById(id) {
    // TODO: Реализовать когда будет ручка на бекенде
    // const response = await fetch(`${API_BASE_URL}/get/${id}`)
    // if (!response.ok) throw new Error('Ошибка получения объекта')
    // return await response.json()
    
    // Временная заглушка
    return {
      id: id,
      name: 'Фродо Бэггинс',
      coordinates: { x: 10, y: 20.5 },
      creationDate: new Date().toISOString(),
      age: 50,
      creatureType: 'HOBBIT',
      creatureLocation: {
        name: 'Шир',
        area: 1000,
        population: 5000,
        establishmentDate: '2024-01-01T00:00:00',
        governor: 'HOBBIT',
        capital: true,
        populationDensity: 5
      },
      attackLevel: 15.5,
      defenseLevel: 25.0,
      ring: {
        name: 'Единое Кольцо',
        power: 9999,
        weight: 0.5
      }
    }
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
    
    return await response.json()
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
    
    return await response.json()
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
  }

  async getAllCreatures() {
    const response = await fetch(`${API_BASE_URL}/getAll`, {
      method: 'GET'
    })
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Ошибка получения объектов')
    }
    
    return await response.json()
  }
}

