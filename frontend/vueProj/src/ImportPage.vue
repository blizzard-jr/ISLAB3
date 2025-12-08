<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-6xl mx-auto space-y-6">
      <!-- Секция загрузки файлов -->
      <div class="bg-white rounded-lg shadow-sm p-6">
        <h2 class="text-2xl font-bold text-gray-900 mb-4">Импорт данных из YAML (≤3 файла)</h2>

        <div
          @dragover.prevent
          @drop.prevent="handleDrop"
          class="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center hover:border-blue-400 transition-colors"
          :class="{ 'border-blue-500 bg-blue-50': isDragging }"
          @dragenter="isDragging = true"
          @dragleave="isDragging = false"
        >
          <input
            ref="fileInput"
            type="file"
            multiple
            accept=".yaml,.yml"
            class="hidden"
            @change="handleFileSelect"
          />
          
          <Upload class="mx-auto h-12 w-12 text-gray-400 mb-4" />
          <p class="text-gray-600 mb-2">
            Перетащите файлы сюда или
            <button @click="fileInput?.click()" class="text-blue-600 hover:text-blue-700 font-medium">
              выберите файлы
            </button>
          </p>
          <p class="text-gray-400 text-sm">Максимум 3 файла формата .yaml или .yml</p>
        </div>

        <!-- Список выбранных файлов -->
        <div v-if="selectedFiles.length > 0" class="mt-4 space-y-2">
          <div
            v-for="(file, index) in selectedFiles"
            :key="index"
            class="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
          >
            <div class="flex items-center gap-3">
              <FileText class="h-5 w-5 text-gray-500" />
              <span class="text-sm font-medium text-gray-700">{{ file.name }}</span>
              <span class="text-xs text-gray-400">{{ formatFileSize(file.size) }}</span>
            </div>
            <button
              @click="removeFile(index)"
              class="p-1 hover:bg-gray-200 rounded transition-colors"
            >
              <X class="h-4 w-4 text-gray-500" />
            </button>
          </div>
        </div>

        <!-- Кнопка импорта -->
        <div class="mt-4 flex gap-4">
          <button
            @click="uploadFiles"
            :disabled="selectedFiles.length === 0 || uploading"
            class="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
          >
            <Loader2 v-if="uploading" class="h-4 w-4 animate-spin" />
            {{ uploading ? 'Загрузка...' : 'Импортировать' }}
          </button>
          
          <button
            @click="downloadConflicts"
            class="px-6 py-2 border border-gray-300 hover:bg-gray-50 text-gray-700 font-medium rounded-lg transition-colors flex items-center gap-2"
          >
            <Download class="h-4 w-4" />
            Скачать лог конфликтов
          </button>
        </div>

        <!-- Сообщение об ошибке/успехе -->
        <div v-if="uploadMessage" class="mt-4 p-3 rounded-lg" :class="uploadSuccess ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200'">
          <p :class="uploadSuccess ? 'text-green-700' : 'text-red-700'" class="text-sm">
            {{ uploadMessage }}
          </p>
        </div>
      </div>

      <!-- История импорта -->
      <div class="bg-white rounded-lg shadow-sm p-6">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-2xl font-bold text-gray-900">История импорта</h2>
          <button
            @click="loadHistory"
            class="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            :disabled="loadingHistory"
          >
            <RefreshCw class="h-5 w-5 text-gray-500" :class="{ 'animate-spin': loadingHistory }" />
          </button>
        </div>

        <div v-if="loadingHistory" class="text-center py-8">
          <Loader2 class="h-8 w-8 animate-spin text-blue-500 mx-auto" />
          <p class="text-gray-500 mt-2">Загрузка истории...</p>
        </div>

        <div v-else-if="history.length === 0" class="text-center py-8 text-gray-500">
          История импорта пуста
        </div>

        <div v-else class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">ID</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Статус</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Пользователь</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Файлов</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Добавлено</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Дата начала</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Дата завершения</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="item in history" :key="item.id" class="hover:bg-gray-50">
                <td class="px-4 py-3 text-sm text-gray-900">{{ item.id }}</td>
                <td class="px-4 py-3 text-sm">
                  <span
                    class="px-2 py-1 rounded-full text-xs font-medium"
                    :class="getStatusClass(item.status)"
                  >
                    {{ getStatusText(item.status) }}
                  </span>
                </td>
                <td class="px-4 py-3 text-sm text-gray-600">{{ item.userId }}</td>
                <td class="px-4 py-3 text-sm text-gray-600">{{ item.fileCount }}</td>
                <td class="px-4 py-3 text-sm text-gray-600">
                  {{ item.status === 'SUCCESS' ? item.addedCount : '-' }}
                </td>
                <td class="px-4 py-3 text-sm text-gray-600">{{ formatDate(item.startTime) }}</td>
                <td class="px-4 py-3 text-sm text-gray-600">{{ formatDate(item.endTime) }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Пагинация -->
        <div v-if="totalPages > 1" class="mt-4 flex justify-center gap-2">
          <button
            @click="changePage(currentPage - 1)"
            :disabled="currentPage === 0"
            class="px-3 py-1 border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50"
          >
            Назад
          </button>
          <span class="px-3 py-1">{{ currentPage + 1 }} / {{ totalPages }}</span>
          <button
            @click="changePage(currentPage + 1)"
            :disabled="currentPage >= totalPages - 1"
            class="px-3 py-1 border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50"
          >
            Вперед
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Upload, FileText, X, Download, Loader2, RefreshCw } from 'lucide-vue-next'
import { api } from './api.js'

const fileInput = ref(null)
const selectedFiles = ref([])
const isDragging = ref(false)
const uploading = ref(false)
const uploadMessage = ref('')
const uploadSuccess = ref(false)

const history = ref([])
const loadingHistory = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)

function handleFileSelect(event) {
  const files = Array.from(event.target.files)
  addFiles(files)
}

function handleDrop(event) {
  isDragging.value = false
  const files = Array.from(event.dataTransfer.files)
  addFiles(files)
}

function addFiles(files) {
  const yamlFiles = files.filter(f => 
    f.name.endsWith('.yaml') || f.name.endsWith('.yml')
  )
  
  if (selectedFiles.value.length + yamlFiles.length < 3 || selectedFiles.value.length + yamlFiles.length > 10) {
    uploadMessage.value = 'Максимум 3 файла'
    uploadSuccess.value = false
    return
  }
  
  selectedFiles.value.push(...yamlFiles)
}

function removeFile(index) {
  selectedFiles.value.splice(index, 1)
}

function formatFileSize(bytes) {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

async function uploadFiles() {
  if (selectedFiles.value.length === 0) return
  
  uploading.value = true
  uploadMessage.value = ''
  
  try {
    const result = await api.uploadImportFiles(selectedFiles.value)
    uploadSuccess.value = true
    uploadMessage.value = `Импорт запущен! ID операции: ${result.id}`
    selectedFiles.value = []
    
    // Обновляем историю
    setTimeout(() => loadHistory(), 2000)
  } catch (error) {
    uploadSuccess.value = false
    uploadMessage.value = error.message
  } finally {
    uploading.value = false
  }
}

async function downloadConflicts() {
  try {
    const blob = await api.downloadConflictFile()
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'conflicts.txt'
    a.click()
    URL.revokeObjectURL(url)
  } catch (error) {
    uploadMessage.value = error.message
    uploadSuccess.value = false
  }
}

async function loadHistory() {
  loadingHistory.value = true
  try {
    const data = await api.getImportHistory(currentPage.value)
    history.value = data.content
    totalPages.value = data.totalPages
  } catch (error) {
    console.error('Failed to load history:', error)
  } finally {
    loadingHistory.value = false
  }
}

function changePage(page) {
  currentPage.value = page
  loadHistory()
}

function getStatusClass(status) {
  switch (status) {
    case 'SUCCESS': return 'bg-green-100 text-green-800'
    case 'FAILED': return 'bg-red-100 text-red-800'
    case 'IN_PROGRESS': return 'bg-blue-100 text-blue-800'
    case 'PENDING': return 'bg-yellow-100 text-yellow-800'
    default: return 'bg-gray-100 text-gray-800'
  }
}

function getStatusText(status) {
  switch (status) {
    case 'SUCCESS': return 'Успешно'
    case 'FAILED': return 'Ошибка'
    case 'IN_PROGRESS': return 'В процессе'
    case 'PENDING': return 'Ожидание'
    default: return status
  }
}

function formatDate(dateString) {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadHistory()
})
</script>
