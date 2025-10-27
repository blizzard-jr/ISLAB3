import { useToast } from '../composables/useToast.js'

const { error: showError, success: showSuccess } = useToast()

export async function parseApiError(response) {
  try {
    const contentType = response.headers.get('content-type')
    
    if (contentType && contentType.includes('application/json')) {
      const errorData = await response.json()
      
      if (errorData.status && errorData.error && errorData.message) {
        return {
          status: errorData.status,
          title: errorData.error,
          message: errorData.message
        }
      }
    }
    
    const text = await response.text()
    return {
      status: response.status,
      title: 'Ошибка',
      message: text || response.statusText
    }
  } catch (e) {
    return {
      status: response.status,
      title: 'Ошибка',
      message: response.statusText
    }
  }
}

export function showApiError(errorData) {
  const message = `<strong>${errorData.title}</strong><br>${errorData.message}`
  showError(message, 7000)
}

export function showApiSuccess(message) {
  showSuccess(message, 3000)
}

export function handleCatchError(error, defaultMessage = 'Произошла ошибка') {
  console.error(error)
  const message = error.message || defaultMessage
  showError(message, 5000)
}
