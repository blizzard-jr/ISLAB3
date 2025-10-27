<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
      <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-900">
          {{ mode === 'create' ? 'Создать кольцо' : 'Редактировать кольцо' }}
        </h2>
        <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700">
          <X :size="24" />
        </button>
      </div>

      <form @submit.prevent="handleSubmit" class="p-6 space-y-6">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            Название <span class="text-red-500">*</span>
          </label>
          <input
            v-model="formData.name"
            type="text"
            required
            minlength="1"
            placeholder="Например: Единое Кольцо"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-500"
          />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Сила <span class="text-red-500">*</span>
            </label>
            <input
              v-model.number="formData.power"
              type="number"
              required
              step="any"
              placeholder="9999"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Вес <span class="text-red-500">*</span>
            </label>
            <input
              v-model.number="formData.weight"
              type="number"
              required
              step="any"
              placeholder="0.5"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-500"
            />
          </div>
        </div>

        <div class="flex justify-end gap-3 pt-4 border-t border-gray-200">
          <button
            type="button"
            @click="$emit('close')"
            class="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
          >
            Отмена
          </button>
          <button
            type="submit"
            class="px-6 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-700 transition-colors flex items-center gap-2"
          >
            <Save :size="18" />
            {{ mode === 'create' ? 'Создать' : 'Сохранить' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { X, Save } from 'lucide-vue-next'
import { api } from './api.js'
import { useToast } from './composables/useToast.js'
import { showApiError, showApiSuccess } from './utils/errorHandler.js'

const { warning } = useToast()

const props = defineProps({
  ringId: {
    type: Number,
    default: null
  },
  mode: {
    type: String,
    default: 'create',
    validator: (value) => ['create', 'edit'].includes(value)
  }
})

const emit = defineEmits(['close', 'saved'])

const formData = ref({
  name: '',
  power: null,
  weight: null
})

async function loadRing() {
  if (!props.ringId) return

  try {
    const data = await api.getRingById(props.ringId)
    formData.value = { ...data }
  } catch (error) {
    console.error('Ошибка загрузки кольца:', error)
    alert('Ошибка при загрузке данных кольца')
  }
}

async function handleSubmit() {
  if (!formData.value.name || formData.value.name.trim() === '') {
    warning('Название кольца не может быть пустым')
    return
  }

  if (!formData.value.power || formData.value.power <= 0) {
    warning('Сила должна быть больше 0')
    return
  }

  if (!formData.value.weight || formData.value.weight <= 0) {
    warning('Вес должен быть больше 0')
    return
  }

  try {
    if (props.mode === 'create') {
      await api.createRing(formData.value)
      showApiSuccess('Кольцо успешно создано!')
    } else {
      await api.updateRing(props.ringId, formData.value)
      showApiSuccess('Кольцо успешно обновлено!')
    }
    emit('saved')
  } catch (error) {
    if (error.status && error.title) {
      showApiError(error)
    } else {
      showApiError({
        title: 'Ошибка',
        message: error.message || 'Произошла ошибка при сохранении'
      })
    }
  }
}

onMounted(() => {
  if (props.mode === 'edit') {
    loadRing()
  }
})
</script>

